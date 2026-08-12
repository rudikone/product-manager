package ru.rudikov.productmanager.exception.auth.handler;

import ru.rudikov.productmanager.exception.auth.domain.reset.password.MissingArgumentsToResetPasswordException;
import ru.rudikov.productmanager.exception.auth.domain.reset.password.PasswordsDoNotMatchException;
import ru.rudikov.productmanager.exception.message.RestErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Обработчик исключений, связанных со сбросом пароля.
 */
@ControllerAdvice
public class ResetPasswordExceptionsHandler extends ResponseEntityExceptionHandler {

    /**
     * Обрабатывает MissingArgumentsToResetPasswordException.
     * Возвращает ответ со статусом 400.
     * @param ex MissingArgumentsToResetPasswordException
     * @return ResponseEntity<RestErrorMessage> со статусом 400 и сообщением исключения
     */
    @ExceptionHandler(MissingArgumentsToResetPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestErrorMessage> handleMissingArgumentsToResetPasswordException(MissingArgumentsToResetPasswordException ex) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatResponse);
    }

    /**
     * Обрабатывает PasswordsDoNotMatchException.
     * Возвращает ответ со статусом 400.
     * @param ex PasswordsDoNotMatchException
     * @return ResponseEntity<RestErrorMessage> со статусом 400 и сообщением исключения
     */
    @ExceptionHandler(PasswordsDoNotMatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestErrorMessage> handlePasswordsDoNotMatchException(PasswordsDoNotMatchException ex) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatResponse);
    }

}
