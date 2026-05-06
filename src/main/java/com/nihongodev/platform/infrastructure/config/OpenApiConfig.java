package com.nihongodev.platform.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI nihongoDevOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NihongoDev Platform API")
                        .description("API for Japanese learning platform for IT developers")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("NihongoDev Team")
                                .email("contact@nihongodev.com"))
                        .license(new License()
                                .name("MIT")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .schemaRequirement("Bearer Authentication",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .bearerFormat("JWT")
                                .scheme("bearer"));
    }
}
