package ru.rudikov.productmanager.auth.services;

import ru.rudikov.productmanager.auth.models.dto.password.PasswordResetDTO;
import ru.rudikov.productmanager.auth.models.dto.password.PasswordResetRequestDTO;

public interface IPasswordResetService {

    void requestReset(PasswordResetRequestDTO data);

    void reset(String email, String token, PasswordResetDTO data);
}
