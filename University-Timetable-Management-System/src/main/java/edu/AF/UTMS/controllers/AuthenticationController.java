package edu.AF.UTMS.controllers;

import edu.AF.UTMS.dto.*;
import edu.AF.UTMS.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        if (authenticationService.emailAlreadyExists(signUpRequestDTO.getEmail())) {
            System.out.println("Email already in use!");
            return new ResponseEntity("Email is already used to signup! Try another...", HttpStatus.BAD_REQUEST);
        } else {
            try {
                return ResponseEntity.ok(authenticationService.signUp(signUpRequestDTO));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (ResponseStatusException e) {
                System.out.println(e.getMessage());
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponseDTO> signIn(@RequestBody SignInRequestDTO signInRequestDTO) {
        return ResponseEntity.ok(authenticationService.signIn(signInRequestDTO));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequestDTO));
    }
}
