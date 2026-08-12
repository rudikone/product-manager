package ru.rudikov.productmanager.auth.services.impl;

import ru.rudikov.productmanager.auth.models.User;
import ru.rudikov.productmanager.auth.repositories.UserRepository;
import ru.rudikov.productmanager.auth.services.IAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Сервис авторизации.
 * Отвечает за обработку операций авторизации пользователей.
 */
@Service
public class AuthorizationService implements IAuthorizationService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Загружает пользователя по имени пользователя.
     * @param username Имя пользователя для загрузки
     * @throws UsernameNotFoundException если пользователь не найден
     * @return Объект UserDetails с информацией о пользователе
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    /**
     * Получает ID текущего авторизованного пользователя.
     * @return ID текущего пользователя
     */
    public long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            User userDetails = (User) authentication.getPrincipal();
            return userDetails.getId();
        }
        return 0;
    }

}