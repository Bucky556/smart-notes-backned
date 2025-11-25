package code.uz.smartnotesbackned.service;

import code.uz.smartnotesbackned.enums.EmailType;
import code.uz.smartnotesbackned.exception.BadException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EmailSendService {
    @Value("${spring.mail.username}")
    private String fromEmail;
    private final JavaMailSender mailSender;
    private final EmailHistoryService emailHistoryService;

    private void sendEmail(String toEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);
            CompletableFuture.runAsync(() ->
                    mailSender.send(message));

        } catch (MessagingException e) {
            throw new BadException("Failed to send email");
        }
    }

    public void sendHtmlEmail(String toEmail, String name, String code, EmailType emailType) {
        try {
            ClassPathResource resource;
            String subject;

            switch (emailType) {
                case REGISTRATION -> {
                    resource = new ClassPathResource("email_registration.html");
                    subject = "Email Verification";
                }
                case RESET_PASSWORD -> {
                    resource = new ClassPathResource("email_reset_password.html");
                    subject = "Reset Your Password";
                }
                case UPDATE_EMAIL -> {
                    resource = new ClassPathResource("email_update.html");
                    subject = "Update Your Email";
                }
                case RESEND_CODE -> {
                    resource = new ClassPathResource("email_resend_code.html");
                    subject = "Resend Your Code";
                }
                default -> throw new BadException("Invalid email type");

            }

            String htmlContent = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
            String htmlBody = htmlContent.replace("{{name}}", name)
                    .replace("{{code}}", code);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            sendEmail(toEmail, name, htmlBody);
            checkAndSaveToDB(toEmail, code, emailType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkAndSaveToDB(String toEmail, String code, EmailType emailType) {
        emailHistoryService.create(toEmail, code, emailType);

        Long emailCount = emailHistoryService.getEmailCountWhileSending(toEmail);
        Integer emailLimit = 3;
        if (emailCount > emailLimit) {
            throw new BadException("Email limit exceeded");
        }

        emailHistoryService.checkCode(toEmail, code);
    }

    public void sendNotificationEmail(String email, String name, String title, String message) {
        try{
            ClassPathResource resource = new ClassPathResource("email_notification.html");
            String htmlContent = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
            String htmlBody = htmlContent.replace("{{name}}", name)
                    .replace("{{email}}", email)
                    .replace("{{title}}", title)
                    .replace("{{message}}", message);

            sendEmail(email, title, htmlBody);

        }catch (IOException e){
            throw new RuntimeException("Failed to load notification email template");
        }
    }
}
