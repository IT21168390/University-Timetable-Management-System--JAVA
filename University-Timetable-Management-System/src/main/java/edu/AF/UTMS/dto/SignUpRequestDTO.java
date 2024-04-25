package edu.AF.UTMS.dto;

import lombok.Data;

@Data
public class SignUpRequestDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    //private UserRoles userRole;

    private String faculty;
}
