package com.ecommerce.docs;


import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "JwtAuth",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "Authorization"
)
public class OpenApiConfig {


    @Bean
    OpenAPI customOpenApi()
    {
        var openApi = new OpenAPI();
        var info = new Info();
        info.setTitle("E-commerce APIs");
        info.setVersion("V1");

        openApi.setInfo(info);

        return openApi;
    }
}
