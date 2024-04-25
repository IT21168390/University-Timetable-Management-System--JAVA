package edu.AF.UTMS.dto;

import lombok.Data;

@Data
public class SignInRequestDTO {
    private String email;
    private String password;
}
