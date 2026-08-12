package com.kawser.cleanspringbootproject.exception.api.domain.product;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Исключение при пустом списке продуктов.
 * Ссылка на сообщение об ошибке в messages.properties: product.empty_list
 */
@Slf4j
public class ProductsEmptyException extends RuntimeException {

    private final static ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

    /**
     * Конструктор исключения при пустом списке продуктов.
     */
    public ProductsEmptyException() {
        super(bundle.getString("product.empty_list"));
        log.error("Products list is empty.");
    }
}
