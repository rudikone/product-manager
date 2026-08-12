package com.kawser.cleanspringbootproject.exception.api.domain.product;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Исключение при отсутствии продукта с указанным id.
 * Ссылка на сообщение об ошибке в messages.properties: product.not_found
 */
@Slf4j
public class ProductNotFoundException extends RuntimeException {

    private final static ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

    /**
     * Конструктор исключения при отсутствии продукта с указанным id.
     *
     * @param id id продукта, который не найден.
     */
    public ProductNotFoundException(Long id) {
        super(bundle.getString("product.not_found").replace("{id}", id.toString()));
        log.error("Product with id {} not found.", id);
    }
}
