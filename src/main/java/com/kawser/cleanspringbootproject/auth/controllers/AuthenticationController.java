package com.kawser.cleanspringbootproject.auth.controllers;

import com.kawser.cleanspringbootproject.auth.models.dto.authentication.LoginDTO;
import com.kawser.cleanspringbootproject.auth.models.dto.authentication.LoginResponseDTO;
import com.kawser.cleanspringbootproject.auth.models.dto.authentication.SignupDTO;
import com.kawser.cleanspringbootproject.auth.services.IAuthenticationService;
import com.kawser.cleanspringbootproject.auth.services.impl.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Контроллер для эндпоинтов аутентификации.
 * Обрабатывает HTTP-запросы, связанные с аутентификацией пользователей.
 * Использует AuthenticationService для выполнения операций с базой данных.
 *
 * @see AuthenticationService
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final IAuthenticationService authService;

    AuthenticationController(IAuthenticationService authService){
        this.authService= authService;
    }

    private final ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

    /**
     * Аутентифицирует пользователя с указанными учётными данными и возвращает токен при успехе.
     * @param data Учётные данные пользователя для аутентификации, передаются в теле запроса.
     * @return Ответ, содержащий токен, если пользователь успешно аутентифицирован.
     */
    @Operation(summary = "Аутентификация пользователя", description = "Аутентификация пользователя с указанными учётными данными и возврат токена при успехе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно аутентифицирован, возвращён токен"),
            @ApiResponse(responseCode = "401", description = "Предоставлены неверные учётные данные"),
            @ApiResponse(responseCode = "400", description = "Пользователь не подтверждён")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody @Valid LoginDTO data) {

        var response = authService.login(data);
        return ResponseEntity.ok(response);
    }

    /**
     * Регистрирует нового пользователя с указанными данными и отправляет email для подтверждения.
     * @param data Данные пользователя для регистрации, передаются в теле запроса.
     * @return Ответ, содержащий сообщение об успешной регистрации.
     */
    @Operation(summary = "Регистрация нового пользователя", description = "Регистрация нового пользователя с указанными данными")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Email или имя пользователя уже существует"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @RequestBody @Valid SignupDTO data) {

        authService.signup(data);
        return ResponseEntity.ok(bundle.getString("user.successfully_signed_up"));
    }

    /**
     * Подтверждает email пользователя с указанными email и токеном.
     * @param email Email пользователя для подтверждения.
     * @param token Токен для подтверждения email пользователя.
     * @return Ответ, содержащий сообщение об успешном подтверждении email.
     */
    @Operation(summary = "Подтверждение email пользователя", description = "Подтверждение email пользователя с указанными email и токеном")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email пользователя успешно подтверждён"),
            @ApiResponse(responseCode = "400", description = "Пользователь уже подтверждён"),
            @ApiResponse(responseCode = "401", description = "Предоставлен неверный токен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping("/verify-account")
    public ResponseEntity<String> verifyAccount(
            @RequestParam String email,
            @RequestParam String token) {

        authService.verifyAccount(email, token);
        return ResponseEntity.ok(bundle.getString("user.successfully_verified"));
    }

    /**
     * Повторно отправляет email для подтверждения пользователю с указанным email.
     * @param email Email пользователя для повторной отправки подтверждения.
     * @return Ответ, содержащий сообщение об успешной повторной отправке.
     */
    @Operation(summary = "Повторная отправка email для подтверждения", description = "Повторная отправка email для подтверждения пользователю с указанным email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email для подтверждения успешно отправлен повторно"),
            @ApiResponse(responseCode = "400", description = "Пользователь уже подтверждён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(
            @RequestParam String email) {

        authService.resendVerification(email);
        return ResponseEntity.ok(bundle.getString("user.verification_email_resent"));
    }
}
