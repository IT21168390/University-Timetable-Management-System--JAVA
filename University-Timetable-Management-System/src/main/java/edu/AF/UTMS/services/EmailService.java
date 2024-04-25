package edu.AF.UTMS.services;

import edu.AF.UTMS.models.consts.UserRoles;

import java.util.List;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendMultipleEmailsRoleBased(List<UserRoles> to, String subject, String body);
    void sendMultipleEmails(List<String> to, String subject, String body);
}
