package ru.rudikov.productmanager.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenAPI (Swagger UI) с поддержкой JWT-аутентификации.
 * Добавляет кнопку "Authorize" в Swagger UI для ввода JWT-токена.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Product Manager API",
                version = "1.0.0",
                description = "REST API для управления продуктами с JWT-аутентификацией",
                contact = @Contact(
                        name = "Product Manager Team",
                        email = "support@example.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development Server"),
                @Server(url = "http://localhost:8081", description = "Local Staging Server")
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Введите JWT-токен, полученный через POST /auth/login. " +
                "Формат: просто токен без префикса 'Bearer '. " +
                "Пример: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
)
public class OpenApiConfig {
    // Конфигурация определяется через аннотации
}
