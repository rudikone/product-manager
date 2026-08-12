package ru.rudikov.productmanager.auth.config.security;

import ru.rudikov.productmanager.auth.models.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Сервис для работы с JWT-токенами.
 * Отвечает за генерацию и верификацию токенов.
 */
@Service
@Slf4j
public class TokenService {

    /**
     * Секретный ключ для генерации токена, загружается из application.properties.
     */
    @Value("${auth.security.token.secret}")
    private String secret;

    /**
     * Время жизни токена в секундах, загружается из application.properties.
     */
    @Value("${auth.security.token.expiration-time}")
    private long EXPIRATION_TIME;

    /**
     * Генерирует JWT-токен для указанного пользователя с временем жизни, заданным в application.properties.
     * @param user Пользователь, для которого генерируется токен
     * @throws RuntimeException Исключение при ошибке генерации токена
     * @return Сгенерированный токен
     */
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            var now = Instant.now();

            String token = JWT.create()
                    .withIssuer("auth-service")
                    .withSubject(user.getUsername())
                    .withExpiresAt(now.plusSeconds(EXPIRATION_TIME))
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            log.error("Error while generating token");
            throw new RuntimeException("Error while generating token");
        }
    }

    /**
     * Проверяет валидность токена и возвращает декодированный subject (username).
     * @param token Токен для проверки
     * @throws JWTVerificationException Исключение при ошибке верификации токена
     * @return Subject токена (username пользователя)
     */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            var verifier = JWT.require(algorithm)
                    .withIssuer("auth-service")
                    .build();
            var decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }

}

