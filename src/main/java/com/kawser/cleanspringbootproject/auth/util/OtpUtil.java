package com.kawser.cleanspringbootproject.auth.util;

import com.kawser.cleanspringbootproject.auth.models.OneTimePassword;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Утилита для генерации и проверки OTP-кодов.
 */
@Component
public class OtpUtil {

    private final int MINUTES_TO_EXPIRE = 5;

    /**
     * Генерирует 6-значный OTP-код.
     * @return Объект OneTimePassword, содержащий OTP и время генерации
     */
    public OneTimePassword generateOtp() {
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(1000000));
        return new OneTimePassword(otp, LocalDateTime.now());
    }

    /**
     * Проверяет, действителен ли ещё OTP-код.
     * @param otp OTP для проверки
     * @return true, если OTP ещё действителен, false в противном случае
     */
    public boolean isValidOtp(OneTimePassword otp) {
        return otp.otpGenerationTime().plusMinutes(MINUTES_TO_EXPIRE).isAfter(LocalDateTime.now());
    }
}
