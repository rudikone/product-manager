package ru.rudikov.productmanager.exception.api.handler;

import ru.rudikov.productmanager.exception.api.domain.product.InvalidProductNameException;
import ru.rudikov.productmanager.exception.api.domain.product.ProductNotFoundException;
import ru.rudikov.productmanager.exception.api.domain.product.ProductsEmptyException;
import ru.rudikov.productmanager.exception.message.RestErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Обработчик исключений, связанных с продуктами.
 */
@ControllerAdvice
public class ProductExceptionsHandler extends ResponseEntityExceptionHandler {

    /**
     * Обрабатывает ProductNotFoundException.
     * Возвращает ответ со статусом 404.
     * @param ex ProductNotFoundException
     * @return ResponseEntity<RestErrorMessage> со статусом 404 и сообщением исключения
     */
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<RestErrorMessage> handleProductNotFoundException(ProductNotFoundException ex) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(threatResponse);
    }

    /**
     * Обрабатывает ProductsEmptyException.
     * Возвращает ответ со статусом 404.
     * @param ex ProductsEmptyException
     * @return ResponseEntity<RestErrorMessage> со статусом 404 и сообщением исключения
     */
    @ExceptionHandler(ProductsEmptyException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<RestErrorMessage> handleProductsEmptyException(ProductsEmptyException ex) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(threatResponse);
    }

    /**
     * Обрабатывает InvalidProductNameException.
     * Возвращает ответ со статусом 400.
     * @param ex InvalidProductNameException
     * @return ResponseEntity<RestErrorMessage> со статусом 400 и сообщением исключения
     */
    @ExceptionHandler(InvalidProductNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestErrorMessage> handleInvalidProductNameException(InvalidProductNameException ex) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatResponse);
    }
}
