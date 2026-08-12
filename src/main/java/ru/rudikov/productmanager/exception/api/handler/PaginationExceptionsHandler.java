package ru.rudikov.productmanager.exception.api.handler;

import ru.rudikov.productmanager.exception.api.domain.pagination.InvalidArgumentsToPaginationException;
import ru.rudikov.productmanager.exception.api.domain.pagination.InvalidSortDirectionException;
import ru.rudikov.productmanager.exception.message.RestErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Обработчик исключений, связанных с пагинацией.
 */
@ControllerAdvice
public class PaginationExceptionsHandler extends ResponseEntityExceptionHandler {

    /**
     * Обрабатывает InvalidArgumentsToPaginationException.
     * Возвращает ответ со статусом 400.
     * @param ex InvalidArgumentsToPaginationException
     * @return ResponseEntity<RestErrorMessage> со статусом 400 и сообщением исключения
     */
    @ExceptionHandler(InvalidArgumentsToPaginationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestErrorMessage> handleInvalidArgumentsToPaginationException(InvalidArgumentsToPaginationException ex) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatResponse);
    }

    /**
     * Обрабатывает InvalidSortDirectionException.
     * Возвращает ответ со статусом 400.
     * @param ex InvalidSortDirectionException
     * @return ResponseEntity<RestErrorMessage> со статусом 400 и сообщением исключения
     */
    @ExceptionHandler(InvalidSortDirectionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestErrorMessage> handleInvalidSortDirectionException(InvalidSortDirectionException ex) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatResponse);
    }

}
