package ru.rudikov.productmanager.auth.services;

import ru.rudikov.productmanager.auth.models.dto.authentication.LoginDTO;
import ru.rudikov.productmanager.auth.models.dto.authentication.LoginResponseDTO;
import ru.rudikov.productmanager.auth.models.dto.authentication.SignupDTO;

public interface IAuthenticationService {

    LoginResponseDTO login(LoginDTO data);

    void signup(SignupDTO data);
}
