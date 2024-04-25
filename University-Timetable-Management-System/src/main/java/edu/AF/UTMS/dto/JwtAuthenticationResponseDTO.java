package edu.AF.UTMS.dto;

import lombok.Data;

@Data
public class JwtAuthenticationResponseDTO {
    private String token;
    private String refreshToken;
    private String email;       //Additional
}
