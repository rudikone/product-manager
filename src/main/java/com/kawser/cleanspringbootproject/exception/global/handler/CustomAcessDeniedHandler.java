package com.kawser.cleanspringbootproject.exception.global.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Обработчик исключений доступа.
 * Реализует AccessDeniedHandler и отвечает за обработку исключений доступа.
 */
public class CustomAcessDeniedHandler implements AccessDeniedHandler {

    ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

    /**
     * Обрабатывает исключения доступа.
     * Возвращает ответ со статусом 403 и сообщением о запрете доступа.
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param accessDeniedException AccessDeniedException
     * @throws IOException
     * @throws ServletException
     * @see AccessDeniedHandler
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(bundle.getString("access.denied"));
    }
}
