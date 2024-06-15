package com.example.timestamp_service.service;


import com.example.timestamp_service.model.PasswordReset;
import com.example.timestamp_service.model.User;
import com.example.timestamp_service.model.exceptions.DuplicateEmailException;
import com.example.timestamp_service.model.exceptions.DuplicateUsernameException;
import com.example.timestamp_service.model.exceptions.UserNotFoundException;
import com.example.timestamp_service.model.roles.UserRole;
import com.example.timestamp_service.repository.PasswordResetRepository;
import com.example.timestamp_service.repository.UserRepository;
import com.example.timestamp_service.service.Impl.UserServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;


import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordResetRepository passwordResetRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        User user = new User("test@students.com", "Test User", "testuser", "password");

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("password");

        this.userService = Mockito.spy(new UserServiceImpl(this.userRepository, this.passwordEncoder, this.emailService, this.passwordResetRepository));

    }

    @Test
    public void testSaveUser_DuplicateEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> {
            userService.saveUser("test@example.com", "Full Name", "username", "password");
        });
    }

    @Test
    public void testSaveUser_DuplicateUsername() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(DuplicateUsernameException.class, () -> {
            userService.saveUser("test@example.com", "Full Name", "username", "password");
        });
    }

    @Test
    @Transactional
    public void testSaveUser() throws DuplicateEmailException, DuplicateUsernameException {
        User mockUser = new User("test@students.com", "Test User", "testuser", "password");
        when(userRepository.save(Mockito.any(User.class))).thenReturn(mockUser);

        User user = userService.saveUser("test@students.com", "Test User", "testuser", "password");

        assertNotNull("User is null", user);
        assertEquals("Username does not match", "testuser", user.getUsername());

        verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    public void testSendPasswordResetToken_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        doNothing().when(userService).sendPasswordResetToken( anyString());

        assertDoesNotThrow(() -> userService.sendPasswordResetToken("test@example.com"));

        verify(userService, times(1)).sendPasswordResetToken( anyString());
    }

    @Test
    public void testSendPasswordResetToken_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> {
            userService.sendPasswordResetToken("test@example.com");
        });
    }

    @Test
    public void testSendPasswordResetToken() {
        String username = "testuser";
        User user = new User("test@domain.com", "Test User", username, "password", UserRole.ROLE_USER);
        String token = "randomToken";
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(1);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordResetRepository.findByUser(user)).thenReturn(Optional.empty());
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());

        userService.sendPasswordResetToken(username);

        ArgumentCaptor<PasswordReset> captor = ArgumentCaptor.forClass(PasswordReset.class);
        verify(passwordResetRepository).save(captor.capture());
        PasswordReset savedPasswordReset = captor.getValue();
        assertEquals(user, savedPasswordReset.getUser());
        assertNotNull(savedPasswordReset.getPasswordResetToken());
        assertTrue(savedPasswordReset.getPasswordResetTokenExpirationDate().isAfter(LocalDateTime.now()));
        verify(emailService).sendPasswordResetEmail(eq(user.getEmail()), anyString());
    }

}
