package ru.rudikov.productmanager.exception.auth.handler;

import ru.rudikov.productmanager.exception.auth.domain.user.EmailAlreadyExistsException;
import ru.rudikov.productmanager.exception.auth.domain.user.UserNotFoundException;
import ru.rudikov.productmanager.exception.auth.domain.user.UsernameAlreadyExistsException;
import ru.rudikov.productmanager.exception.message.RestErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Обработчик исключений, связанных с сущностью пользователя.
 */
@ControllerAdvice
public class UserExceptionsHandler extends ResponseEntityExceptionHandler {

    /**
     * Обрабатывает UserNotFoundException.
     * Возвращает ответ со статусом 404.
     * @param ex UserNotFoundException
     * @return ResponseEntity<RestErrorMessage> со статусом 404 и сообщением исключения
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<RestErrorMessage> handleUserNotFoundException(UserNotFoundException ex) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(threatResponse);
    }

    /**
     * Обрабатывает EmailAlreadyExistsException.
     * Возвращает ответ со статусом 400.
     * @param ex EmailAlreadyExistsException
     * @return ResponseEntity<RestErrorMessage> со статусом 400 и сообщением исключения
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestErrorMessage> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatResponse);
    }

    /**
     * Обрабатывает UsernameAlreadyExistsException.
     * Возвращает ответ со статусом 400.
     * @param ex UsernameAlreadyExistsException
     * @return ResponseEntity<RestErrorMessage> со статусом 400 и сообщением исключения
     */
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestErrorMessage> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatResponse);
    }
}
