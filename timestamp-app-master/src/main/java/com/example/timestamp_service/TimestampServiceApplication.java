package com.example.timestamp_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Properties;

@SpringBootApplication
@EnableAsync
public class TimestampServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimestampServiceApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("gracielastoimenova@gmail.com");
        mailSender.setPassword("laks cbfm cuxx otoa");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    public KeyStore keyStore() throws Exception{
        String keystorePath = "///C:/Users/graci/root_ca/ib_ca/timestamps.p12";
        String keystorePassword = "timestamp";

        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(fis, keystorePassword.toCharArray());
            return keyStore;
        }
    }

}

