package com.ecommerce;

import com.ecommerce.Category.services.ProductCategoryCachingProxy;
import com.ecommerce.Images.entity.ProductImages;
import com.ecommerce.Images.services.ProductImagesCachingService;
import com.ecommerce.Product.services.crud.ProductCrudService;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;


@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisSerializer<Object> jdkValueSerializer() {
        return new JdkSerializationRedisSerializer();
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer cacheConfiguration(RedisSerializer<Object> jdkValueSerializer) {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();

        return builder -> {
            builder
                    .cacheDefaults(
                            RedisCacheConfiguration.defaultCacheConfig()
                                    .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringSerializer))
                                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jdkValueSerializer))
                                    .entryTtl(Duration.ofDays(1))
                    );
            builder.withCacheConfiguration(ProductCrudService.CACHE_NAME,RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(1)));
            builder.withCacheConfiguration(ProductCategoryCachingProxy.CACHE_NAME,RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(3)));
            builder.withCacheConfiguration(ProductImagesCachingService.CACHE_NAME,RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(3)));
        };

    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            RedisSerializer<Object> jdkValueSerializer) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jdkValueSerializer);
        template.setHashValueSerializer(jdkValueSerializer);

        template.afterPropertiesSet();
        return template;
    }
}