package edu.AF.UTMS.services.impl;

import edu.AF.UTMS.dto.*;
import edu.AF.UTMS.services.AuthenticationService;
import edu.AF.UTMS.services.JWTService;
import edu.AF.UTMS.models.User;
import edu.AF.UTMS.models.consts.UserRoles;
import edu.AF.UTMS.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    private final CommonDataServiceImpl commonDataServiceImpl;

    public UserDTO signUp(SignUpRequestDTO signUpRequestDTO) {
        List<String> faculties = null;
        try {
            faculties = commonDataServiceImpl.getFacultyList();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        User user = new User();

        if (signUpRequestDTO.getEmail() == null) {
            throw new IllegalArgumentException("Email is required!");
        } else {
            user.setEmail(signUpRequestDTO.getEmail());
        }
        user.setFirstName(signUpRequestDTO.getFirstName());
        user.setLastName(signUpRequestDTO.getLastName());
        user.setUserRole(UserRoles.STUDENT);
        user.setPassword(passwordEncoder.encode(signUpRequestDTO.getPassword()));
        if (user.getUserRole().equals(UserRoles.STUDENT)) {
            if (faculties.contains(signUpRequestDTO.getFaculty())) {
                user.setFaculty(signUpRequestDTO.getFaculty());
            } else {
                throw new IllegalArgumentException("Invalid Faculty Name!");
            }
        }
        //return userRepository.save(user);
        User createdUser = userRepository.save(user);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(createdUser.getId());
        userDTO.setLastName(createdUser.getLastName());
        userDTO.setEmail(createdUser.getEmail());
        userDTO.setFirstName(createdUser.getFirstName());
        userDTO.setUserRole(createdUser.getUserRole());
        if (createdUser.getFaculty() != null) {
            userDTO.setFaculty(createdUser.getFaculty());
        }

        return userDTO;
    }

    public boolean emailAlreadyExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public JwtAuthenticationResponseDTO signIn(SignInRequestDTO signInRequest) throws IllegalArgumentException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword()));
        var user = userRepository.findByEmail(signInRequest.getEmail());
        //UserDetails userDetails = userRepository.findByEmail(signInRequest.getEmail());
        //UserDTO userDTO = new UserDTO();
        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        JwtAuthenticationResponseDTO jwtAuthenticationResponseDTO = new JwtAuthenticationResponseDTO();
        jwtAuthenticationResponseDTO.setToken(jwt);
        jwtAuthenticationResponseDTO.setRefreshToken(refreshToken);
        jwtAuthenticationResponseDTO.setEmail(user.getUsername());

        return jwtAuthenticationResponseDTO;
    }

    public JwtAuthenticationResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
        String user_Email = jwtService.extractUserName(refreshTokenRequestDTO.getToken());
        UserDetails user = userRepository.findByEmail(user_Email);
        if (jwtService.isTokenValid(refreshTokenRequestDTO.getToken(), user)){
            var jwt = jwtService.generateToken(user);

            JwtAuthenticationResponseDTO jwtAuthenticationResponseDTO = new JwtAuthenticationResponseDTO();
            jwtAuthenticationResponseDTO.setToken(jwt);
            jwtAuthenticationResponseDTO.setRefreshToken(refreshTokenRequestDTO.getToken());
            return jwtAuthenticationResponseDTO;
        }
        return null;
    }
}
