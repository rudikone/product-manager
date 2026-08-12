package com.kawser.cleanspringbootproject.auth.services.impl;


import com.kawser.cleanspringbootproject.auth.models.dto.password.PasswordResetDTO;
import com.kawser.cleanspringbootproject.auth.models.dto.password.PasswordResetRequestDTO;
import com.kawser.cleanspringbootproject.auth.repositories.UserRepository;
import com.kawser.cleanspringbootproject.auth.services.IPasswordResetService;
import com.kawser.cleanspringbootproject.auth.util.EmailUtil;
import com.kawser.cleanspringbootproject.auth.util.OtpUtil;
import com.kawser.cleanspringbootproject.exception.auth.domain.authentication.InvalidOtpException;
import com.kawser.cleanspringbootproject.exception.auth.domain.reset.password.MissingArgumentsToResetPasswordException;
import com.kawser.cleanspringbootproject.exception.auth.domain.reset.password.PasswordsDoNotMatchException;
import com.kawser.cleanspringbootproject.exception.auth.domain.user.UserNotEnabledException;
import com.kawser.cleanspringbootproject.exception.auth.domain.user.UserNotFoundException;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Сервис сброса пароля.
 * Отвечает за обработку операций сброса пароля.
 */
@Service
@Slf4j
public class PasswordResetService implements IPasswordResetService {

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private OtpUtil otpUtil;

    @Autowired
    private UserRepository userRepository;

    /**
     * Запрос на сброс пароля по email.
     * @param data DTO с email пользователя, который хочет сбросить пароль
     * @throws UserNotFoundException если пользователь с указанным email не найден
     * @throws UserNotEnabledException если пользователь не активирован
     * @throws MessagingException если произошла ошибка при отправке email
     */
    public void requestReset(PasswordResetRequestDTO data) {
        log.info("Received data to request a password reset");

        var user = userRepository.findByEmail(data.email())
                .orElseThrow(() -> new UserNotFoundException(data.email()));

        if (!user.isEnabled()) {
            throw new UserNotEnabledException();
        }

        var oneTimePassword = otpUtil.generateOtp();
        user.setOtp(oneTimePassword);

        userRepository.save(user);

        try {
            emailUtil.sendRecoverPasswordEmail(data.email(), oneTimePassword.otp());
        } catch (MessagingException e) {
            log.error("Error occurred while sending email to reset password");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error occurred while sending email to reset password");
        }
    }

    /**
     * Сброс пароля пользователя.
     * @param email email пользователя для сброса пароля
     * @param token одноразовый пароль для сброса пароля
     * @param data DTO с новым паролем
     * @throws ResponseStatusException если OTP или новый пароль не предоставлены
     * @throws UserNotFoundException если пользователь с указанным email не найден
     * @throws InvalidOtpException если OTP неверен или истёк
     * @throws PasswordsDoNotMatchException если новый пароль и подтверждение не совпадают
     */
    public void reset(String email, String token, PasswordResetDTO data) {
        log.info("Received data to reset a password");

        if (token == null || data == null || data.password() == null || data.confirmPassword() == null) {
            throw new MissingArgumentsToResetPasswordException();
        }

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (!user.getOtp().otp().equals(token) || !otpUtil.isValidOtp(user.getOtp())) {
            throw new InvalidOtpException(new Throwable("Invalid or expired OTP"));
        } else {

            if (!data.password().equals(data.confirmPassword())) {
                throw new PasswordsDoNotMatchException();
            }

            String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
            user.setPassword(encryptedPassword);
            user.setOtp(null);

            userRepository.save(user);
        }
    }
}
