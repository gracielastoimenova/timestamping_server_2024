package com.example.timestamp_service.service;


import com.example.timestamp_service.config.MailConfigurationProperties;
import com.example.timestamp_service.service.EmailService;
import com.example.timestamp_service.service.Impl.EmailServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MailConfigurationProperties mailConfigurationProperties;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        this.emailService = Mockito.spy(new EmailServiceImpl(this.mailSender,this.mailConfigurationProperties));
    }

    @Test
    public void testSendEmail() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(mailConfigurationProperties.getUsername()).thenReturn("from@example.com");

        emailService.sendEmail(to, subject, content);

        verify(mailSender).send(mimeMessage);
    }
}
