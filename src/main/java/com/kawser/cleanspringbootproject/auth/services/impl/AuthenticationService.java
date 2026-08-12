package com.kawser.cleanspringbootproject.auth.services.impl;

import com.kawser.cleanspringbootproject.auth.config.security.TokenService;
import com.kawser.cleanspringbootproject.auth.models.OneTimePassword;
import com.kawser.cleanspringbootproject.auth.models.User;
import com.kawser.cleanspringbootproject.auth.models.UserRole;
import com.kawser.cleanspringbootproject.auth.models.dto.authentication.LoginDTO;
import com.kawser.cleanspringbootproject.auth.models.dto.authentication.LoginResponseDTO;
import com.kawser.cleanspringbootproject.auth.models.dto.authentication.SignupDTO;
import com.kawser.cleanspringbootproject.auth.repositories.UserRepository;
import com.kawser.cleanspringbootproject.auth.services.IAuthenticationService;
import com.kawser.cleanspringbootproject.auth.util.EmailUtil;
import com.kawser.cleanspringbootproject.auth.util.OtpUtil;
import com.kawser.cleanspringbootproject.exception.auth.domain.authentication.InvalidCredentialsException;
import com.kawser.cleanspringbootproject.exception.auth.domain.authentication.InvalidOtpException;
import com.kawser.cleanspringbootproject.exception.auth.domain.user.*;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private OtpUtil otpUtil;

    /**
     * Аутентифицирует пользователя и генерирует JWT-токен.
     * Выбрасывает исключение, если пользователь отключен или учётные данные неверны.
     *
     * @param data Данные для входа (логин и пароль).
     * @throws UserNotEnabledException если пользователь не активирован.
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
        } catch (DisabledException ex) {
            throw new UserNotEnabledException();
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException();
        } catch (Exception ex) {
            log.error("Error occurred during login");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred during login");
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

        OneTimePassword oneTimePassword = otpUtil.generateOtp();
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        User user = User.builder()
                .username(data.username())
                .password(encryptedPassword)
                .email(data.email())
                .mobilePhone(data.mobilePhone())
                .role(UserRole.USER)
                .otp(oneTimePassword)
                .build();

        log.info("New user created: {}", data.username());

        userRepository.save(user);

        try {
            emailUtil.sendOtpEmail(data.email(), oneTimePassword.otp());
        } catch (MessagingException ex) {
            log.error("Error occurred while sending email to verify account");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error occurred while sending email to verify account");
        }
    }

    /**
     * Подтверждает учётную запись пользователя по email и OTP.
     * Выбрасывает исключение, если пользователь уже подтверждён, OTP неверен или истёк, или пользователь не найден.
     * @param email Email пользователя.
     * @param otp OTP-код для подтверждения.
     * @throws UserAlreadyVerifiedException если пользователь уже подтверждён.
     * @throws InvalidOtpException если OTP неверен или истёк.
     */
    public void verifyAccount(String email, String otp) {
        log.info("Received email and OTP to verify account");

        userRepository.findByEmail(email)
                .ifPresentOrElse(user -> {
                    log.info("User found: {}", user.getUsername());
                    if (user.isEnabled()) {
                        throw new UserAlreadyVerifiedException();
                    }

                    if (user.getOtp().otp().equals(otp) && otpUtil.isValidOtp(user.getOtp())) {
                        user.setEnabled(true);
                        user.setOtp(null);
                        userRepository.save(user);
                        log.info("User account verified");
                    } else {
                        throw new InvalidOtpException(new Throwable("Invalid or expired OTP"));
                    }
                }, () -> {
                    throw new UserNotFoundException(email);
                });
    }

    /**
     * Повторно отправляет email для подтверждения учётной записи.
     * Выбрасывает исключение, если пользователь уже подтверждён или не найден.
     * @param email Email пользователя.
     * @throws UserAlreadyVerifiedException если пользователь уже подтверждён.
     * @throws UserNotFoundException если пользователь не найден.
     */
    public void resendVerification(String email) {
        log.info("Received email to resend verification email");

        userRepository.findByEmail(email)
                .ifPresentOrElse(user -> {
                    log.info("User found: {}", user.getUsername());
                    if (user.isEnabled()) {
                        throw new UserAlreadyVerifiedException();
                    }

                    OneTimePassword oneTimePassword = otpUtil.generateOtp();
                    try {
                        emailUtil.sendOtpEmail(email, oneTimePassword.otp());
                    } catch (MessagingException ex) {
                        log.error("Error occurred while sending email to verify account");
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                "Error occurred while sending email to verify account");
                    }
                    user.setOtp(oneTimePassword);
                    userRepository.save(user);
                }, () -> {
                    throw new UserNotFoundException(email);
                });
    }

}