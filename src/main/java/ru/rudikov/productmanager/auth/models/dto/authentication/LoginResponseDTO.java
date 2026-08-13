package ru.rudikov.productmanager.auth.models.dto.authentication;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для ответа на запрос входа.
 * Содержит токен аутентифицированного пользователя.
 * Используется для передачи данных между контроллером и сервисом.
 * @see LoginDTO
 */
@Schema(description = "Ответ на успешную аутентификацию")
public record LoginResponseDTO(
        @Schema(description = "JWT-токен для авторизованных запросов", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {

}
