package com.kawser.cleanspringbootproject.auth.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Утилита для отправки email.
 * Отвечает за отправку email-сообщений.
 */
@Component
public class EmailUtil {

    /**
     * Экземпляр JavaMailSender для отправки email.
     */
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Отправляет email с OTP для подтверждения учётной записи.
     * @param email email для отправки OTP
     * @param otp OTP для подтверждения учётной записи
     * @throws MessagingException если произошла ошибка при отправке email
     */
    public void sendOtpEmail(String email, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("Подтвердите ваш аккаунт");
        helper.setText(
                        """
                        <div>
                        <a href="https://localhost:8080/auth/verify-account?email=%s&token=%s">
                        Нажмите здесь для подтверждения аккаунта
                        </a>
                        </div>
                        """
                        .formatted(email, otp),
                true);

        mailSender.send(message);
    }

    /**
     * Отправляет email с OTP для восстановления пароля.
     * @param email email для отправки OTP
     * @param otp OTP для восстановления пароля
     * @throws MessagingException если произошла ошибка при отправке email
     */
    public void sendRecoverPasswordEmail(String email, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("Восстановление пароля");
        helper.setText(
                """
                        <div>
                        <a href="http://localhost:8080/password/reset?email=%s&token=%s">
                        Нажмите здесь для установки нового пароля
                        </a>
                        </div>
                        """
                        .formatted(email, otp),
                true);

        mailSender.send(message);
    }
}
