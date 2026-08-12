package ru.rudikov.productmanager.auth.models.dto.authentication;

/**
 * DTO для аутентификации пользователя.
 * Содержит имя пользователя и пароль.
 * Используется для передачи данных между контроллером и сервисом.
 * @see LoginResponseDTO
 */
public record LoginDTO(String username, String password) {

}
