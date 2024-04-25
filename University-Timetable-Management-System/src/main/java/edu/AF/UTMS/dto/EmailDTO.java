package edu.AF.UTMS.dto;

import edu.AF.UTMS.models.consts.UserRoles;
import lombok.Data;

import java.util.List;

@Data
public class EmailDTO {
    private List<UserRoles> emailTo;
    private String subject;
    private String message;
}
