package ru.rudikov.productmanager.exception.auth.handler;

import ru.rudikov.productmanager.exception.auth.domain.authentication.InvalidCredentialsException;
import ru.rudikov.productmanager.exception.message.RestErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Обработчик исключений, связанных с аутентификацией.
 */
@ControllerAdvice
public class AuthExceptionsHandler extends ResponseEntityExceptionHandler {

    /**
     * Обрабатывает InvalidCredentialsException.
     * Возвращает ответ со статусом 401.
     * @param ex InvalidCredentialsException
     * @return ResponseEntity<RestErrorMessage> со статусом 401 и сообщением исключения
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<RestErrorMessage> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(threatResponse);
    }
}
