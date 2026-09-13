package com.ecommerce;

import java.util.Collection;
import java.util.List;
import java.util.Set;


public class CacheUtils {

    public static final String CacheName_RawProduct = "products";
    public static final String CacheName_ProductImage = CacheName_RawProduct+":images";
    public static final String CacheName_ProductCategories = CacheName_RawProduct + ":categories";

    public static class RawProductUtils{
        public static Long redisKeyToId(String redisId)
        {
            int lastIdx = redisId.lastIndexOf(":");

            return Long.parseLong(redisId.substring(lastIdx+1));
        }

        public static String toRedisKey(Long id)
        {
            return CacheName_RawProduct + "::"+id;
        }
        public static List<String> toRedisKeys(Collection<Long> ids)
        {
            return ids.stream().map(RawProductUtils::toRedisKey).toList();
        }
        public static List<String> toRedisKeys(Set<Long> ids)
        {
            return ids.stream().map(RawProductUtils::toRedisKey).toList();
        }
    }

    public static class ProductCategoriesUtils{
        public static Long redisKeyToId(String redisId)
        {
            int lastIdx = redisId.lastIndexOf(":");

            return Long.parseLong(redisId.substring(lastIdx+1));
        }

        public static String toRedisKey(Long id)
        {
            return CacheName_ProductCategories + "::"+id;
        }
        public static List<String> toRedisKeys(Collection<Long> ids)
        {
            return ids.stream().map(ProductCategoriesUtils::toRedisKey).toList();
        }
    }

    public static class ProductImagesUtils{
        public static Long redisKeyToId(String redisId)
        {
            int lastIdx = redisId.lastIndexOf(":");

            return Long.parseLong(redisId.substring(lastIdx+1));
        }

        public static String toRedisKey(Long id)
        {
            return CacheName_ProductImage + "::"+id;
        }
        public static List<String> toRedisKeys(Collection<Long> ids)
        {
            return ids.stream().map(ProductImagesUtils::toRedisKey).toList();
        }
    }
}
