package ru.rudikov.productmanager.auth.models;

import ru.rudikov.productmanager.auth.util.validator.ValidPassword;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * Сущность пользователя, реализующая интерфейс UserDetails для использования Spring Security.
 * Каждый пользователь имеет id, имя пользователя, пароль, email, мобильный телефон и роль.
 *
 * @see UserRole
 * @see UserDetails
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Users")
@Builder
public class User implements UserDetails {

    /**
     * ID пользователя. Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Логин пользователя. Должен быть уникальным и не пустым.
     * Должен содержать от 5 до 15 символов.
     */
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 5, max = 15, message = "Username must be between 5 and 15 characters long")
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Пароль пользователя. Должен быть валидным согласно аннотации ValidPassword.
     * @see ValidPassword
     */
    @ValidPassword
    private String password;

    /**
     * Email пользователя. Должен быть уникальным, валидным и не пустым.
     */
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Мобильный телефон пользователя. Должен содержать 11 цифр.
     */
    @Pattern(regexp = "^[0-9]{11}$", message = "User mobile phone must have 11 digits")
    private String mobilePhone;

    /**
     * Роль пользователя. Это enum UserRole, который может быть USER или ADMIN.
     */
    private UserRole role;

    /**
     * Конструктор для сущности User.
     *
     * @param username Логин пользователя.
     * @param password Пароль пользователя.
     * @param email Email пользователя.
     * @param role Роль пользователя.
     * @param mobilePhone Мобильный телефон пользователя.
     */
    public User(String username, String password, String email, UserRole role, String mobilePhone) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.mobilePhone = mobilePhone;
    }

    /**
     * Возвращает права доступа пользователя.
     * Если пользователь является администратором, возвращает ROLE_ADMIN и ROLE_USER.
     * Если пользователь не администратор, возвращает только ROLE_USER.
     *
     * @return Коллекция прав доступа пользователя.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN) {
            return Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            return Set.of(() -> "ROLE_USER");
        }
    }

    /**
     * Возвращает имя пользователя.
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * Возвращает true, если учётная запись не истекла.
     *
     * @return по умолчанию true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Возвращает true, если учётная запись не заблокирована.
     *
     * @return по умолчанию true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Возвращает true, если учётные данные не истекли.
     *
     * @return по умолчанию true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Возвращает true, если пользователь активен.
     * Всегда возвращает true, так как верификация через OTP удалена.
     *
     * @return true
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
