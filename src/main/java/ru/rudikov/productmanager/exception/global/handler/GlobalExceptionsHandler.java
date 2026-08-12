package ru.rudikov.productmanager.exception.global.handler;

import ru.rudikov.productmanager.exception.api.domain.common.ModelValidationException;
import ru.rudikov.productmanager.exception.message.RestErrorMessage;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений.
 * Отвечает за обработку исключений на уровне приложения.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionsHandler {

    /**
     * Обрабатывает исключение MethodArgumentNotValidException.
     * Возвращает ответ со статусом 400.
     * Сообщение об ошибке содержит поля и соответствующие сообщения об ошибках.
     * Формат сообщения: {field1=message1, field2=message2, ...}
     * @param ex MethodArgumentNotValidException
     * @param request WebRequest
     * @return ResponseEntity<RestErrorMessage> со статусом 400 и сообщением с полями и их ошибками
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
            WebRequest request) {

        // Получает ошибки полей и помещает их в карту
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        // Формат сообщения: {field1=message1, field2=message2, ...}
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("{");
        errors.forEach((key, value) -> errorMessage.append(key).append("=").append(value).append(", "));
        if (errorMessage.length() > 1) { // Проверяет, есть ли сообщения об ошибках
            errorMessage.setLength(errorMessage.length() - 2); // Удаляет последнюю запятую и пробел ", "
        }
        errorMessage.append("}");

        // Создаёт ответ с сообщением об ошибке
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST, errorMessage.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatResponse);
    }

    /**
     * Обрабатывает исключение ConstraintViolationException.
     * Возвращает ответ со статусом 400.
     * Сообщение об ошибке содержит поля и соответствующие сообщения об ошибках.
     * Формат сообщения: {field1=message1, field2=message2, ...}
     * @param ex ConstraintViolationException
     * @param request WebRequest
     * @return ResponseEntity<RestErrorMessage> со статусом 400 и сообщением с полями и их ошибками
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestErrorMessage> handleConstraintViolationException(ConstraintViolationException ex,
            WebRequest request) {

        // Получает нарушения ограничений и помещает их в карту
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations()
                .forEach(violation -> errors.put(violation.getPropertyPath().toString(), violation.getMessage()));

        // Формат сообщения: {field1=message1, field2=message2, ...}
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("{");
        errors.forEach((key, value) -> errorMessage.append(key).append("=").append(value).append(", "));
        if (errorMessage.length() > 1) { // Проверяет, есть ли сообщения об ошибках
            errorMessage.setLength(errorMessage.length() - 2); // Удаляет последнюю запятую и пробел ", "
        }
        errorMessage.append("}");

        // Создаёт ответ с сообщением об ошибке
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST, errorMessage.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatResponse);
    }

    /**
     * Обрабатывает исключение ResponseStatusException.
     * Возвращает ответ со статусом и причиной.
     * @param ex ResponseStatusException
     * @return ResponseEntity<RestErrorMessage> со статусом и причиной
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<RestErrorMessage> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatusCode httpStatusCode = ex.getStatusCode();
        HttpStatus httpStatus = HttpStatus.valueOf(httpStatusCode.value());

        // Создаёт ответ с сообщением об ошибке
        RestErrorMessage threatResponse = new RestErrorMessage(httpStatus, ex.getReason());
        return ResponseEntity.status(httpStatus).body(threatResponse);
    }

    @ExceptionHandler(ModelValidationException.class)
    public ResponseEntity<String> handleModelValidationException(ModelValidationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}