package com.kawser.cleanspringbootproject.auth.models.dto.password;

import com.kawser.cleanspringbootproject.auth.util.validator.ValidPassword;

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
