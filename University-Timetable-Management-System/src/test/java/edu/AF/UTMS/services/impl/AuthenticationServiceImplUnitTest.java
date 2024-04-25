package edu.AF.UTMS.services.impl;

import edu.AF.UTMS.dto.JwtAuthenticationResponseDTO;
import edu.AF.UTMS.dto.SignInRequestDTO;
import edu.AF.UTMS.dto.SignUpRequestDTO;
import edu.AF.UTMS.dto.UserDTO;
import edu.AF.UTMS.models.User;
import edu.AF.UTMS.models.consts.Faculties;
import edu.AF.UTMS.models.consts.UserRoles;
import edu.AF.UTMS.repositories.UserRepository;
import edu.AF.UTMS.services.AuthenticationService;
import edu.AF.UTMS.services.CommonDataService;
import edu.AF.UTMS.services.JWTService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class AuthenticationServiceImplUnitTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    public AuthenticationServiceImplUnitTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void emailAlreadyExists_NonExistingEmail_ReturnsFalse() {
        // Arrange
        String nonExistingEmail = "nonexisting@example.com";

        // Act
        boolean result = authenticationService.emailAlreadyExists(nonExistingEmail);

        // Assert
        assertFalse(result);
    }
}
