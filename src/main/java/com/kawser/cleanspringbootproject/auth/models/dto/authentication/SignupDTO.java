package com.kawser.cleanspringbootproject.auth.models.dto.authentication;

import com.kawser.cleanspringbootproject.auth.util.validator.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO для регистрации пользователя.
 * Содержит имя пользователя, пароль, email и мобильный телефон.
 * Используется для передачи данных между контроллером и сервисом.
 */
public record SignupDTO(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 5, max = 15, message = "Username must be between 5 and 15 characters long")
        String username,

        @NotBlank(message = "Password cannot be blank")
        @ValidPassword // @ValidPassword — пользовательская аннотация валидации
        String password,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email should be valid")
        String email,

        @Pattern(regexp = "^[0-9]{11}$", message = "User mobile phone must have 11 digits")
        String mobilePhone
) {}

