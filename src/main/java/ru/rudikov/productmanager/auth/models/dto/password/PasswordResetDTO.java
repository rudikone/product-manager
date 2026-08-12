package ru.rudikov.productmanager.auth.models.dto.password;

import ru.rudikov.productmanager.auth.util.validator.ValidPassword;

/**
 * DTO для сброса пароля пользователя.
 * Содержит пароль и подтверждение пароля.
 * Используется для передачи данных между контроллером и сервисом.
 * @see PasswordResetRequestDTO
 */
public record PasswordResetDTO (
                        @ValidPassword String password,
                        String confirmPassword) {

}
