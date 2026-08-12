package ru.rudikov.productmanager.auth.models.dto.authentication;

/**
 * DTO для ответа на запрос входа.
 * Содержит токен аутентифицированного пользователя.
 * Используется для передачи данных между контроллером и сервисом.
 * @see LoginDTO
 */
public record LoginResponseDTO(String token) {

}
