package com.hesmantech.salonbooking.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    static final String BEARER_TOKEN_KEY = "bearerToken";

    private final String appTitle;
    private final String appVersion;

    public OpenAPIConfig(@Value("${app.title}") String appTitle,
                         @Value("${app.version}") String appVersion) {
        this.appTitle = appTitle;
        this.appVersion = appVersion;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(appTitle)
                        .version(appVersion))
                .components(new Components()
                        .addSecuritySchemes(BEARER_TOKEN_KEY,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .security(List.of(new SecurityRequirement().addList(BEARER_TOKEN_KEY)));
    }
}
