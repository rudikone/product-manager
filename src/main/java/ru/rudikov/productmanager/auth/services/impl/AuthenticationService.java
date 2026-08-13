package ru.rudikov.productmanager.auth.services.impl;

import ru.rudikov.productmanager.auth.config.security.TokenService;
import ru.rudikov.productmanager.auth.models.User;
import ru.rudikov.productmanager.auth.models.UserRole;
import ru.rudikov.productmanager.auth.models.dto.authentication.LoginDTO;
import ru.rudikov.productmanager.auth.models.dto.authentication.LoginResponseDTO;
import ru.rudikov.productmanager.auth.models.dto.authentication.SignupDTO;
import ru.rudikov.productmanager.auth.repositories.UserRepository;
import ru.rudikov.productmanager.auth.services.IAuthenticationService;
import ru.rudikov.productmanager.exception.auth.domain.authentication.InvalidCredentialsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.rudikov.productmanager.exception.auth.domain.user.EmailAlreadyExistsException;
import ru.rudikov.productmanager.exception.auth.domain.user.UsernameAlreadyExistsException;

/**
 * Сервис аутентификации.
 * Отвечает за обработку операций аутентификации пользователей.
 */
@Service
@Slf4j
public class AuthenticationService implements IAuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Аутентифицирует пользователя и генерирует JWT-токен.
     *
     * @param data Данные для входа (логин и пароль).
     * @throws InvalidCredentialsException если учётные данные неверны.
     * @return Токен для авторизованного пользователя.
     */
    public LoginResponseDTO login(LoginDTO data) {
        log.info("Received data to login");

        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
            var auth = authenticationManager.authenticate(usernamePassword);

            var token = tokenService.generateToken((User) auth.getPrincipal());

            LoginResponseDTO response = new LoginResponseDTO(token);

            log.info("User logged in: {}", response);

            return response;
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException();
        } catch (Exception ex) {
            log.error("Error occurred during login");
            throw new RuntimeException("Error occurred during login");
        }
    }

    /**
     * Регистрирует нового пользователя в системе.
     * Выбрасывает исключение, если имя пользователя или email уже существуют.
     *
     * @param data Данные для регистрации.
     * @throws UsernameAlreadyExistsException если имя пользователя уже существует.
     * @throws EmailAlreadyExistsException если email уже существует.
     */
    public void signup(SignupDTO data) {
        log.info("Received data to signup");

        if (userRepository.existsByUsername(data.username())) {
            throw new UsernameAlreadyExistsException(data.username());
        }

        if (userRepository.existsByEmail(data.email())) {
            throw new EmailAlreadyExistsException(data.email());
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        User user = User.builder()
                .username(data.username())
                .password(encryptedPassword)
                .email(data.email())
                .mobilePhone(data.mobilePhone())
                .role(UserRole.USER)
                .build();

        log.info("New user created: {}", data.username());

        userRepository.save(user);
    }

}