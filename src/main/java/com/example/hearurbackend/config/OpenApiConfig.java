package com.example.hearurbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "JWT";

        SecurityScheme securityScheme = new SecurityScheme()
                .name("access") // HTTP 헤더 이름 설정
                .type(SecurityScheme.Type.APIKEY) // API 키 유형으로 변경
                .in(SecurityScheme.In.HEADER) // 위치는 헤더
                .description("JWT Token without Bearer");

        Info info = new Info()
                .title("HealthHola 백엔드 API")
                .version("1.0")
                .description("구현중");

        Components components = new Components()
                .addSecuritySchemes(securitySchemeName, securityScheme);

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);

        return new OpenAPI()
                .components(components)
                .addSecurityItem(securityRequirement)
                .info(info);
    }
}
