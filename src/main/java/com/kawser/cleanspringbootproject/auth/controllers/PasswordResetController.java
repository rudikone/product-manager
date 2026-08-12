package com.kawser.cleanspringbootproject.auth.controllers;

import com.kawser.cleanspringbootproject.auth.models.dto.password.PasswordResetDTO;
import com.kawser.cleanspringbootproject.auth.models.dto.password.PasswordResetRequestDTO;
import com.kawser.cleanspringbootproject.auth.services.IPasswordResetService;
import com.kawser.cleanspringbootproject.auth.services.impl.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Контроллер для эндпоинтов сброса пароля.
 * Обрабатывает HTTP-запросы, связанные со сбросом пароля.
 * Использует PasswordResetService для выполнения операций с базой данных.
 *
 * @see PasswordResetService
 */
@RestController
@RequestMapping("/password")
public class PasswordResetController {

    private final IPasswordResetService passwordResetService;

    PasswordResetController(IPasswordResetService passwordResetService){
        this.passwordResetService= passwordResetService;
    }

    private final ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

    /**
     * Запрос на сброс пароля для указанного email.
     * @param data Email пользователя для запроса сброса пароля, передаётся в теле запроса.
     * @return Ответ, содержащий сообщение об успешном запросе сброса пароля.
     */
    @Operation(summary = "Запрос на сброс пароля", description = "Запрос на сброс пароля для указанного email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос на сброс пароля успешно создан"),
            @ApiResponse(responseCode = "404", description = "Пользователь с указанным email не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/request-reset")
    public ResponseEntity<String> requestReset(
            @RequestBody @Valid PasswordResetRequestDTO data) {

        passwordResetService.requestReset(data);
        return ResponseEntity.ok(bundle.getString("password_reset.requested"));
    }

    /**
     * Сброс пароля для указанного email и токена.
     * @param email Email пользователя для сброса пароля.
     * @param token Токен, отправленный на email пользователя.
     * @param data Новый пароль и подтверждение пароля, передаются в теле запроса.
     * @return Ответ, содержащий сообщение об успешном сбросе пароля.
     */
    @Operation(summary = "Сброс пароля", description = "Сброс пароля для указанного email и токена")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пароль успешно сброшен"),
        @ApiResponse(responseCode = "400", description = "Необходимо предоставить OTP и новый пароль / пароли не совпадают"),
        @ApiResponse(responseCode = "401", description = "Неверный или истёкший OTP"),
        @ApiResponse(responseCode = "404", description = "Пользователь с указанным email не найден"),
        @ApiResponse(responseCode = "409", description = "Пароли не совпадают"),
    })
    @PostMapping("/reset")
    public ResponseEntity<String> reset(
            @RequestParam String email,
            @RequestParam String token,
            @RequestBody @Valid PasswordResetDTO data) {

        passwordResetService.reset(email, token, data);
        return ResponseEntity.ok(bundle.getString("password_reset.successful"));
    }
}
