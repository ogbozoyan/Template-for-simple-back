package com.og.templateback.configuration.swagger;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author ogbozoyan
 * @since 15.09.2023
 */
@Configuration
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfiguration {
    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${spring.application.version}")
    private String applicationVersion;

    @Bean
    public OpenAPI springDocOpenApi() {
        return new OpenAPI()
                .info(springDocapiInfo())
                .security(List.of(new SecurityRequirement()));

    }

    Info springDocapiInfo() {
        return new Info()
                .description(applicationName)
                .version(applicationVersion);

    }
}
