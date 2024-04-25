package edu.AF.UTMS.services.impl;

import edu.AF.UTMS.models.User;
import edu.AF.UTMS.models.consts.UserRoles;
import edu.AF.UTMS.repositories.UserRepository;
import edu.AF.UTMS.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {
    private JavaMailSender mailSender;
    @Autowired
    private UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    public EmailServiceImpl(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    @Async
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setFrom("it21168390@my.sliit.lk");
        mailMessage.setSubject(subject);
        mailMessage.setText(body);

        try {
            mailSender.send(mailMessage);
            System.out.println("An Email sent to : "+to);
        } catch (MailSendException e) {
            logger.warn(to+" is not a valid Email!");
            System.out.println(e.getMessage());
        } catch (Exception ex) {
            System.out.println("EXCEPTION:- "+ex.getMessage());
        }
    }

    public void sendMultipleEmailsRoleBased(List<UserRoles> to, String subject, String body) {
        if (to.size()>0) {
            for (UserRoles userRole: to) {
                List<User> userList = userRepository.findAllByUserRole(userRole);
                List<String> emailAddresses = new ArrayList<>();

                for (User user: userList) {
                    emailAddresses.add(user.getEmail());
                }
                System.out.println("***** Sending emails to "+userRole+"s *****");
                for (String emailAddress: emailAddresses) {
                    sendEmail(emailAddress, subject, body);
                }
            }
            System.out.println("------------  ALL MAILS SENT!  ------------");
        }
    }

    public void sendMultipleEmails(List<String> to, String subject, String body) {
        System.out.println("***** Sending Email Notifications regarding '"+subject+"' *****");
        for (String emailAddress: to) {
            sendEmail(emailAddress, subject, body);
        }
        System.out.println("------------  ALL MAILS SENT!  ------------");
    }
}
