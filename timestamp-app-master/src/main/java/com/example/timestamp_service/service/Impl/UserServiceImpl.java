package com.example.timestamp_service.service.Impl;

import com.example.timestamp_service.model.PasswordReset;
import com.example.timestamp_service.model.User;
import com.example.timestamp_service.model.exceptions.DuplicateEmailException;
import com.example.timestamp_service.model.exceptions.DuplicateUsernameException;
import com.example.timestamp_service.model.roles.UserRole;
import com.example.timestamp_service.model.exceptions.UserNotFoundException;
import com.example.timestamp_service.repository.PasswordResetRepository;
import com.example.timestamp_service.repository.UserRepository;
import com.example.timestamp_service.service.EmailService;
import com.example.timestamp_service.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetRepository passwordResetRepository;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, PasswordResetRepository passwordResetRepository) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetRepository = passwordResetRepository;
    }
    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public User saveUser(String email, String fullname, String username, String password) throws DuplicateEmailException, DuplicateUsernameException {
            if (userRepository.existsByEmail(email)) throw new DuplicateEmailException();
            if (userRepository.existsByUsername(username)) throw new DuplicateUsernameException();

            return  userRepository.save(new User(email, fullname, username, encodePassword(password)));
    }

    @Override
    @Transactional
    public void sendPasswordResetToken(String username) {
        String token = generateRandomString(16);
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        LocalDateTime passwordResetTokenTimestamp = LocalDateTime.now().plusMinutes(1);

        if (passwordResetRepository.findByUser(user).isPresent()){
            PasswordReset passwordReset = passwordResetRepository.findByUser(user).orElseThrow(UserNotFoundException::new);
            passwordReset.setPasswordResetToken(token);
            passwordReset.setPasswordResetTokenExpirationDate(passwordResetTokenTimestamp);
            passwordResetRepository.save(passwordReset);
        }
        else passwordResetRepository.save(new PasswordReset(user, token, passwordResetTokenTimestamp));

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Override
    public void cleanPasswordToken(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        PasswordReset passwordReset = passwordResetRepository.findByUser(user).orElseThrow(UserNotFoundException::new);
        passwordReset.setPasswordResetTokenExpirationDate(null);
        passwordReset.setPasswordResetToken(null);
        passwordResetRepository.save(passwordReset);
    }

    @Override
    public List<User> getAllUsersWithoutRole() {
        return userRepository.findAllByRoleNull();
    }

    @Override
    public void setRole(String username, String role) {
        User user = findByUsername(username);
        user.setRole(UserRole.valueOf(role));
        userRepository.save(user);
    }

    @Override
    public String geUserEmail(String username) {
        return findByUsername(username).getEmail();
    }

    @Override
    public boolean validateUsernameAndPasswordResetToken(String username, String token) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        PasswordReset passwordReset = passwordResetRepository.findByUser(user).orElseThrow(UserNotFoundException::new);

        if (passwordReset.getPasswordResetTokenExpirationDate().isAfter(LocalDateTime.now()))
            return token.equals(passwordReset.getPasswordResetToken());
        else {
            passwordReset.setPasswordResetToken(null);
            passwordReset.setPasswordResetTokenExpirationDate(null);
            passwordResetRepository.save(passwordReset);
            return false;
        }

    }

    @Override
    public void changePassword(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        user.setPassword(encodePassword(password));
        userRepository.save(user);
    }

    @Override
    public String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    @Override
    public void setVerificationCode(User user, String code) {
        user.setVerificationCode(code);
        userRepository.save(user);
    }

    @Override
    public void sendVerificationCode(User user) {
        String random = generateRandomString(16);
        setVerificationCode(user, random);
        emailService.sendEmail(user.getEmail(),"timestamps app", "Email verification code: "+random);
    }

    @Override
    public User findByCode(String code) {
        return userRepository.findUserByVerificationCode(code).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public void setIsValidated(User user) {
        user.setValidated(true);
        userRepository.save(user);
    }


    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException(username));
    }

    @Override
    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        String role = user.getRole().toString().replace("ROLE_", "");

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(role)
                .build();
    }


}
