package com.ecommerce.Product.services.query;

import com.ecommerce.Product.dtos.ProductRawDto;
import com.ecommerce.util.advices.ReqCollapsing;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.ecommerce.Product.services.crud.ProductCrudService.CACHE_NAME;

@Primary
@Service
public class ProductRawQueryCaching implements ProductQueryService<ProductRawDto>{

    private final ProductQueryService<ProductRawDto> productQueryService;
    private final RedisTemplate<String,Object> redisTemplate;
    private final ObjectMapper objectMapper;
    public ProductRawQueryCaching(@Qualifier("productRawQueryImpl") ProductQueryService<ProductRawDto> productQueryService, RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.productQueryService = productQueryService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public static Long toProductId(String redisId)
    {
        int lastIdx = redisId.lastIndexOf(":");

        return Long.parseLong(redisId.substring(lastIdx+1));
    }
    public static String toRedisKey(Long id)
    {
        return CACHE_NAME + "::"+id;
    }
    public static List<String> toRedisKeys(Collection<Long>ids)
    {
        return ids.stream().map(ProductRawQueryCaching::toRedisKey).toList();
    }

    @Override
    @ReqCollapsing(keys = {"#productId"})
    @Cacheable(value = CACHE_NAME,key = "#productId")
    public ProductRawDto findById(Long productId) {
        return productQueryService.findById(productId);
    }

    @Override
    @ReqCollapsing(keys = {"#ids"})
    public Map<Long, ProductRawDto> findAllByIds(Set<Long> ids) {
        Map<Long, ProductRawDto> resultMap = new HashMap<>();
        List<String>redisKeys = toRedisKeys(ids);
        List<Object> objectList =  redisTemplate.opsForValue().multiGet(redisKeys);
        if(objectList == null)
        {
            return productQueryService.findAllByIds(ids);
        }
        Set<Long> nonExistingIds = new HashSet<>(ids.size());
        int i = 0;
        long productId;
        Object rawCached;
        ProductRawDto value;
        // get products ids for elements not cached in redis and add cached elements to result map
        for(;i < redisKeys.size() ; ++i)
        {
            rawCached = objectList.get(i);
            productId = toProductId(redisKeys.get(i));
            if(rawCached== null)
                nonExistingIds.add(productId);
            else {
                value = objectMapper.convertValue(rawCached,ProductRawDto.class);
                resultMap.put(productId,value);
            }

        }

        // get element from db then cache them back
        if(!nonExistingIds.isEmpty())
        {
            var fromDbResult = productQueryService.findAllByIds(nonExistingIds);
            if(fromDbResult.isEmpty())
                return resultMap;

            resultMap.putAll(fromDbResult);

            // convert map to redis key and value
            Map<String,ProductRawDto>redisMap = new HashMap<>(fromDbResult.size());
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
