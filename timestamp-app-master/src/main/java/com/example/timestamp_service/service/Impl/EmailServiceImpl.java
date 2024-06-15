package com.example.timestamp_service.service.Impl;

import com.example.timestamp_service.config.MailConfigurationProperties;
import com.example.timestamp_service.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final MailConfigurationProperties mailConfigurationProperties;
    private final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    public EmailServiceImpl(JavaMailSender mailSender, MailConfigurationProperties mailConfigurationProperties) {
        this.mailSender = mailSender;
        this.mailConfigurationProperties = mailConfigurationProperties;
    }

    public void sendEmail(String to, String subject, String content) {
        logger.info("Sending email from [{}] to [{}] with subject [{}].",
                mailConfigurationProperties.getUsername(), to, subject);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        try {
            helper.setText(content, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(mailConfigurationProperties.getUsername());
            mailSender.send(mimeMessage);
            logger.info("Successfully sent mail to: " + to);
        } catch (MessagingException e) {
            logger.error("Failed to send email", e);
        }

    }

    public void sendPasswordResetEmail(String email, String token) {
        sendEmail(email, "Forgotten password", getPasswordResetContent(token));
    }

    public String getPasswordResetContent(String token) {
        return "Password reset code: " + token;
    }
}
