package ru.rudikov.productmanager.auth.controllers;

import ru.rudikov.productmanager.auth.models.dto.authentication.LoginDTO;
import ru.rudikov.productmanager.auth.models.dto.authentication.LoginResponseDTO;
import ru.rudikov.productmanager.auth.models.dto.authentication.SignupDTO;
import ru.rudikov.productmanager.auth.services.IAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
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
@Tag(name = "Authentication", description = "Эндпоинты аутентификации и регистрации пользователей")
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
            @ApiResponse(responseCode = "200", description = "Пользователь успешно аутентифицирован, возвращён токен",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginResponseDTO.class))),
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
     * Регистрирует нового пользователя с указанными данными.
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
}
