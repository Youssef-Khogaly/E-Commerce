package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;



@SpringBootApplication
@EntityScan(basePackages = "com.ecommerce.*")
@EnableWebSecurity(debug = true)
@EnableRedisRepositories
@EnableCaching
public class Application {

	public static void main(String[] args) {
        var springContext = SpringApplication.run(Application.class, args);
    }

}
