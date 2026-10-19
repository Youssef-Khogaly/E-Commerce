package com.ecommerce;

import com.fayupable.snowflake.SnowflakeIdGenerator;
import com.fayupable.snowflake.SystemClock;
import com.fayupable.snowflake.config.SnowflakeConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.time.Instant;


@SpringBootApplication
@EntityScan(basePackages = "com.ecommerce.*")
@EnableWebSecurity
@EnableRedisRepositories
@EnableCaching
public class Application {

	public static void main(String[] args) {
        var springContext = SpringApplication.run(Application.class, args);
    }

    @Primary
    @Bean
    SnowflakeIdGenerator snowflakeIdGenerator(){
        SnowflakeConfig config = SnowflakeConfig.defaultConfig(
                Instant.parse("2024-01-01T00:00:00Z").toEpochMilli(), // epoch
                1L, // datacenter id
                1L                                                    // worker id
        );
        return new SnowflakeIdGenerator(config,new SystemClock());
    }
}
