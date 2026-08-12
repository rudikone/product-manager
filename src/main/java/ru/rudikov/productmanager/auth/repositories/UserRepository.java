package ru.rudikov.productmanager.auth.repositories;

import ru.rudikov.productmanager.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * Репозиторий для сущности User.
 */
public interface UserRepository extends JpaRepository<User, Long>{

    /**
     * Находит пользователя по имени пользователя.
     *
     * @param username Имя пользователя.
     * @return Объект UserDetails пользователя.
     */
    UserDetails findByUsername(String username);

    /**
     * Находит пользователя по email.
     *
     * @param email Email пользователя.
     * @return Объект пользователя.
     */
    Optional<User> findByEmail(String email);

    /**
     * Проверяет существование пользователя с указанным именем в базе данных.
     *
     * @param username Имя пользователя.
     */
    boolean existsByUsername(String username);

    /**
     * Проверяет существование пользователя с указанным email в базе данных.
     *
     * @param email Email пользователя.
     */
    boolean existsByEmail(String email);
}
