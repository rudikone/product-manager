package ru.rudikov.productmanager.auth.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Валидатор ограничения для пароля.
 * Пароль должен содержать минимум 8 символов, одну заглавную букву, одну строчную букву, одну цифру и один специальный символ.
 */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    /**
     * Проверяет валидность пароля.
     * @param value Пароль для проверки
     * @param context Контекст, в котором оценивается ограничение
     * @return true, если пароль валиден, false в противном случае
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.length() >= 8 &&
                value.matches(".*[A-Z].*") &&
                value.matches(".*[a-z].*") &&
                value.matches(".*[0-9].*") &&
                value.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }
}