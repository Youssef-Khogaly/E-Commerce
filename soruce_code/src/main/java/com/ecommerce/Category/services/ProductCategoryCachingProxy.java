package com.ecommerce.Category.services;

import com.ecommerce.Category.entity.Category;
import com.ecommerce.Product.services.crud.ProductCrudService;
import com.ecommerce.util.advices.ReqCollapsing;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
@Primary
public class ProductCategoryCachingProxy implements ProductCategoryService{

    public static final String CACHE_NAME = ProductCrudService.CACHE_NAME + ":categories";
    private final ProductCategoryService productCategoryService;
    private final RedisTemplate<String,Object> redisTemplate;
    private final ObjectMapper objectMapper;
    public ProductCategoryCachingProxy(@Qualifier("productCategoryServiceImpl") ProductCategoryService productCategoryService, RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.productCategoryService = productCategoryService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }
    @CacheEvict(value = CACHE_NAME,key = "#productId")
    @Override
    public void putProductCategories(Long productId, Set<Integer> categoriesIds) {
        productCategoryService.putProductCategories(productId,categoriesIds);
    }

    @ReqCollapsing(keys = {"#productId"})
    @Cacheable(value = CACHE_NAME,key = "#productId")
    @Override
    public Collection<Category> getProductCategories(Long productId) {

        return productCategoryService.getProductCategories(productId);
    }

    public static String productIdToRedisKey(Long productId)
    {
        return CACHE_NAME+"::"+productId;
    }
    public static List<String> productIdsToRedisKey(Collection<Long> productIds)
    {
        return productIds.stream().map(ProductCategoryCachingProxy::productIdToRedisKey).collect(toList());
    }
    public static Long fromRedisKeyToId(String key)
    {
        int lastIdx = key.lastIndexOf(":");
        return Long.parseLong(key.substring(lastIdx+1));
    }

    @ReqCollapsing(keys = {"#productIds"})
    @Override
    public Map<Long, Collection<Category>> getProductsCategories(Set<Long> productIds) {

        Map<Long,Collection<Category>> resultMap = new HashMap<>();
        List<String>redisKeys = productIdsToRedisKey(productIds);
        List<Object> objectList =  redisTemplate.opsForValue().multiGet(redisKeys);
        if(objectList == null)
        {
            return productCategoryService.getProductsCategories(productIds);
        }
        Set<Long> nonExistingIds = new HashSet<>(productIds.size());
        int i = 0;
        long productId;
        Object rawCached;
        Set<Category>value;
        // get products ids for elements not cached in redis and add cached elements to result map
        for(;i < redisKeys.size() ; ++i)
        {
            rawCached = objectList.get(i);
            productId = fromRedisKeyToId(redisKeys.get(i));
            if(rawCached== null)
                nonExistingIds.add(productId);
            else {
                value = objectMapper.convertValue(rawCached, new TypeReference<Set<Category>>() {});
                resultMap.put(productId,value);
            }

        }

        // get element from db then cache them back
        if(!nonExistingIds.isEmpty())
        {
            var fromDbResult = productCategoryService.getProductsCategories(nonExistingIds);
            if(fromDbResult.isEmpty())
                return resultMap;

            resultMap.putAll(fromDbResult);

            // convert map to redis key and value
            Map<String,Collection<Category>>redisMap = new HashMap<>(fromDbResult.size());
            for(var entry : fromDbResult.entrySet())
            {
                redisMap.put(productIdToRedisKey(entry.getKey()),entry.getValue());
            }
            // cache result
            redisTemplate.opsForValue().multiSet(redisMap);
        }
        return resultMap;
    }
}
