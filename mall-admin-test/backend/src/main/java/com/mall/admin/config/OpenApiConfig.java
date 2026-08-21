package com.mall.admin.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mallOpenAPI() {
        final String scheme = "bearerAuth";
        SecurityScheme bearer = new SecurityScheme()
                .name(scheme)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(new Info()
                        .title("mall-admin-test API")
                        .description("电商后台练习项目（测试工程师 SUT）")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(scheme))
                .components(new Components().securitySchemes(Map.of(scheme, bearer)));
    }
}
