package com.kawser.cleanspringbootproject.exception.api.domain.pagination;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Исключение при неверном направлении сортировки.
 * Примеры неверных направлений: значения, отличные от "asc" и "desc".
 */
@Slf4j
public class InvalidSortDirectionException extends RuntimeException {

    private final static ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

    /**
     * Конструктор исключения при неверном направлении сортировки.
     */
    public InvalidSortDirectionException() {
        super(bundle.getString("pagination.invalid_sort_direction"));
        log.error(bundle.getString("pagination.invalid_sort_direction"));
    }
}
