package com.kawser.cleanspringbootproject.exception.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Класс сообщения об ошибке REST.
 * Отвечает за создание сообщения, возвращаемого в теле ответа при возникновении исключения.
 * Содержит статус и сообщение.
 */
@AllArgsConstructor
@Getter
@Setter
public class RestErrorMessage {

    /**
     * Статус, возвращаемый в ответе.
     */
    private HttpStatus status;

    /**
     * Сообщение, возвращаемое в теле ответа.
     */
    private String message;

}
