package edu.AF.UTMS.services;

import edu.AF.UTMS.dto.*;

public interface AuthenticationService {
    //User signUp(SignUpRequestDTO signUpRequestDTO);
    UserDTO signUp(SignUpRequestDTO signUpRequestDTO);

    JwtAuthenticationResponseDTO signIn(SignInRequestDTO signInRequest);

    boolean emailAlreadyExists(String email);

    JwtAuthenticationResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO);
}
