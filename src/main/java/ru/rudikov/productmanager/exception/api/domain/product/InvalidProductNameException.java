package ru.rudikov.productmanager.exception.api.domain.product;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Исключение при создании продукта с неверным именем.
 * Ссылка на сообщение об ошибке в messages.properties: product.invalid_name
 */
@Slf4j
public class InvalidProductNameException extends RuntimeException {

    private final static ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

    /**
     * Конструктор исключения при создании продукта с неверным именем.
     *
     * @param name Неверное имя продукта.
     */
    public InvalidProductNameException(String name) {
        super(bundle.getString("product.invalid_name").replace("{name}", name));
        log.error("Invalid product name: {}", name);
    }
}