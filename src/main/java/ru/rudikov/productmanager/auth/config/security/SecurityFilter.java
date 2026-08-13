package ru.rudikov.productmanager.auth.config.security;

import ru.rudikov.productmanager.auth.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр для перехвата запросов и применения правил безопасности.
 */
@Component
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Этот метод вызывается для каждого запроса и отвечает за применение правил безопасности.
     * Токен извлекается из запроса и проверяется с помощью TokenService,
     * затем пользователь загружается из базы данных и устанавливается в SecurityContext.
     * @param request Запрос для фильтрации
     * @param response Ответ для фильтрации
     * @param filterChain Цепочка фильтров для применения
     * @throws ServletException Исключение при ошибке
     * @throws IOException Исключение при ошибке
     * @see TokenService
     * @see UserRepository
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var token = recoverToken(request);

        if (token != null) {
            var login = tokenService.validateToken(token);
            log.debug("Token validated for user: {}", login);

            UserDetails user = userRepository.findByUsername(login);
            
            if (user != null) {
                log.debug("User found: {}, authorities: {}", user.getUsername(), user.getAuthorities());
                
                // Создаёт токен аутентификации и устанавливает в SecurityContext
                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authentication set in SecurityContext");
            } else {
                log.warn("User not found for username: {}", login);
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Метод для извлечения токена из запроса.
     * @param request Запрос для извлечения токена
     * @return Извлечённый токен или null, если заголовок отсутствует, пуст или не начинается с "Bearer "
     */
    private String recoverToken(HttpServletRequest request) {
        var token = request.getHeader("Authorization");
        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            return null;
        }
        return token.replace("Bearer ", "");
    }

}
