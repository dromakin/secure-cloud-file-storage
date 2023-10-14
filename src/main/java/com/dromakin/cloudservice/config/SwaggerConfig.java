/*
 * File:     SwaggerConfig
 * Package:  com.dromakin.springbootldap.config
 * Project:  spring-boot-ldap
 *
 * Created by dromakin as 10.09.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.09.10
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package com.dromakin.cloudservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {
    public static final String AUTH_SECURITY_SCHEME = "spring_token";

    @Value("${spring.application.name}")
    private String applicationName;

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title(applicationName));
    }

    @Bean
    public GroupedOpenApi customFileApi() {
        return GroupedOpenApi.builder().group("file").pathsToMatch("/cloud/file").build();
    }

    @Bean
    public GroupedOpenApi customFilesListApi() {
        return GroupedOpenApi.builder().group("files").pathsToMatch("/cloud/list").build();
    }

    @Bean
    public GroupedOpenApi customLoginApi() {
        return GroupedOpenApi.builder().group("auth").pathsToMatch("/cloud/log**").build();
    }

    @Bean
    public GroupedOpenApi actuatorApi() {
        return GroupedOpenApi.builder().group("actuator").pathsToMatch("/actuator/**").build();
    }
}
