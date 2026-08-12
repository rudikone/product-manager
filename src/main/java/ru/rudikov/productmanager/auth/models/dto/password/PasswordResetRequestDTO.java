package ru.rudikov.productmanager.auth.models.dto.password;

/**
 * DTO для запроса сброса пароля.
 * Содержит email пользователя, который хочет сбросить пароль.
 * Используется для передачи данных между контроллером и сервисом.
 * @see PasswordResetDTO
 */
public record PasswordResetRequestDTO(String email) {
}
