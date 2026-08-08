package com.ecommerce;

import com.ecommerce.entities.user.UserRoles;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;



@SpringBootApplication
@EntityScan(basePackages = "com.ecommerce.entities")
@EnableWebSecurity(debug = true)
public class Application {

	public static void main(String[] args) {
        var springContext = SpringApplication.run(Application.class, args);
    }

}
