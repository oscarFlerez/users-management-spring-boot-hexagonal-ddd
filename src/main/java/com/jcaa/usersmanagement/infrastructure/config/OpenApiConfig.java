package com.jcaa.usersmanagement.infrastructure.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configures the authentication mechanism exposed by OpenAPI and Swagger UI. */
@Configuration(proxyBeanMethods = false)
@SecurityScheme(
    name = OpenApiConfig.BEARER_AUTH,
    description = "JWT obtenido en POST /api/auth/login. Ingrese solamente el token.",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT")
public class OpenApiConfig {

  public static final String BEARER_AUTH = "bearerAuth";

  private OpenApiConfig() {}

  @Bean
  public OpenAPI usersManagementOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Gestion de Usuarios API")
                .version("2.2.0")
                .description("API REST de usuarios con autenticacion y autorizacion por JWT."));
  }
}
