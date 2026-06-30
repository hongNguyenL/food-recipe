package com.nguyen.foodrecipe.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Springdoc OpenAPI / Swagger UI.
 * <p>
 * Swagger UI is available at {@code /swagger-ui.html}.
 * The OpenAPI spec is served at {@code /api-docs}.
 * </p>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI foodRecipeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Food Recipe API")
                        .description("Production-quality REST API for a Food Recipe Website")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Nguyen")
                                .email("contact@nguyen.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
