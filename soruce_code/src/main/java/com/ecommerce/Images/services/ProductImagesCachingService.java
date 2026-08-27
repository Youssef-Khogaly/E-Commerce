package com.ecommerce.Images.services;

import com.ecommerce.Images.dtos.ProductImageDto;
import com.ecommerce.Product.services.crud.ProductCrudService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Primary
public class ProductImagesCachingService implements ProductImagesService{

    private final ProductImagesService productImagesService;
    private final RedisTemplate<String,Object>redisTemplate;
    private final ObjectMapper objectMapper;
    public static final String CACHE_NAME = ProductCrudService.CACHE_NAME+":images";

    public ProductImagesCachingService(@Qualifier("productImageServiceImpl") ProductImagesService productImagesService, RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.productImagesService = productImagesService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    @Cacheable(value = CACHE_NAME,key = "#productId",unless = "#result == null")
    public List<ProductImageDto> getProductImages(Long productId) {
        return productImagesService.getProductImages(productId);
    }

    @CacheEvict(value = CACHE_NAME,key = "#productId")
    @Override
    public void putProductImages(Long productId, Set<Long> imagesIds, Long mainImageId) {
        productImagesService.putProductImages(productId,imagesIds,mainImageId);
    }

    public static Long toProductId(String redisKey)
    {
        int lastIdx = redisKey.lastIndexOf(":");

        return Long.parseLong(redisKey.substring(lastIdx+1));
    }
    public static String toRedisKey(Long productId)
    {
        return CACHE_NAME + "::"+productId;
    }
    public List<String> toRedisKeys(Collection<Long> productIds)
    {
        return productIds.stream().map(ProductImagesCachingService::toRedisKey).toList();
    }
    @Override
    public Map<Long, List<ProductImageDto>> getproductsImages(Set<Long> productIds) {
        Map<Long,List<ProductImageDto>> resultMap = new HashMap<>();
        List<String>redisKeys = toRedisKeys(productIds);
        List<Object> objectList =  redisTemplate.opsForValue().multiGet(redisKeys);
        if(objectList == null)
        {
            return productImagesService.getproductsImages(productIds);
        }
        Set<Long> nonExistingIds = new HashSet<>(productIds.size());
        int i = 0;
        long productId;
        Object rawCached;
        List<ProductImageDto>value;
        // get products ids for elements not cached in redis and add cached elements to result map
        for(;i < redisKeys.size() ; ++i)
        {
            rawCached = objectList.get(i);
            productId = toProductId(redisKeys.get(i));
            if(rawCached== null)
                nonExistingIds.add(productId);
            else {
                value = objectMapper.convertValue(rawCached, new TypeReference<List<ProductImageDto>>() {});
                resultMap.put(productId,value);
            }

        }

        // get element from db then cache them back
        if(!nonExistingIds.isEmpty())
        {
            var fromDbResult = productImagesService.getproductsImages(nonExistingIds);
            if(fromDbResult.isEmpty())
                return resultMap;

            resultMap.putAll(fromDbResult);

            // convert map to redis key and value
            Map<String,List<ProductImageDto>>redisMap = new HashMap<>(fromDbResult.size());
            for(var entry : fromDbResult.entrySet())
            {
                redisMap.put(toRedisKey(entry.getKey()),entry.getValue());
            }
            // cache result
            redisTemplate.opsForValue().multiSet(redisMap);
        }
        return resultMap;
    }
}
