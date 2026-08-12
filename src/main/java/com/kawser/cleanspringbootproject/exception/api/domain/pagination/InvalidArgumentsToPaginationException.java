package com.kawser.cleanspringbootproject.exception.api.domain.pagination;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Исключение при неверных аргументах пагинации.
 * Примеры неверных аргументов: отрицательные номера страниц и отрицательные размеры.
 */
@Slf4j
public class InvalidArgumentsToPaginationException extends RuntimeException {

    private final static ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

    /**
     * Конструктор исключения при неверных аргументах пагинации.
     */
    public InvalidArgumentsToPaginationException() {
        super(bundle.getString("pagination.invalid_arguments"));
        log.error(bundle.getString("pagination.invalid_arguments"));
    }
}
