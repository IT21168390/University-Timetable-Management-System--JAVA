package edu.AF.UTMS.controllers;

import edu.AF.UTMS.dto.EmailDTO;
import edu.AF.UTMS.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/faculty/announcements")
public class AnnouncementsController {
    @Autowired
    private EmailService emailService;

    @PostMapping
    public void sendAnnouncementEmails(@RequestBody EmailDTO emailDTO) {
        try {
            emailService.sendMultipleEmailsRoleBased(emailDTO.getEmailTo(), emailDTO.getSubject(), emailDTO.getMessage());
        } catch (MailException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
