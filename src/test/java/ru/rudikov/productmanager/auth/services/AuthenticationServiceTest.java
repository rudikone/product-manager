package ru.rudikov.productmanager.auth.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import ru.rudikov.productmanager.auth.models.User;
import ru.rudikov.productmanager.auth.models.dto.authentication.SignupDTO;
import ru.rudikov.productmanager.auth.repositories.UserRepository;
import ru.rudikov.productmanager.auth.services.impl.AuthenticationService;
import ru.rudikov.productmanager.exception.auth.domain.user.EmailAlreadyExistsException;
import ru.rudikov.productmanager.exception.auth.domain.user.UsernameAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSignupUserLoginAlreadyExists() {
        SignupDTO data = new SignupDTO("login_test", "PasswordA12@", "test@test.com", "01912990007");

        when(userRepository.existsByUsername(data.username())).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> authenticationService.signup(data));

        verify(userRepository, times(1)).existsByUsername(data.username());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testSignupUserEmailAlreadyExists() {
        SignupDTO data = new SignupDTO("login_test", "PasswordA12@", "test@test.com", "01912990007");

        when(userRepository.existsByUsername(data.username())).thenReturn(false);
        when(userRepository.existsByEmail(data.email())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authenticationService.signup(data));

        verify(userRepository, times(1)).existsByUsername(data.username());
        verify(userRepository, times(1)).existsByEmail(data.email());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testSignupSuccess() {
        SignupDTO data = new SignupDTO("login_test", "PasswordA12@", "test@test.com", "01912990007");

        when(userRepository.existsByUsername(data.username())).thenReturn(false);
        when(userRepository.existsByEmail(data.email())).thenReturn(false);

        authenticationService.signup(data);

        verify(userRepository, times(1)).existsByUsername(data.username());
        verify(userRepository, times(1)).existsByEmail(data.email());
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

}
