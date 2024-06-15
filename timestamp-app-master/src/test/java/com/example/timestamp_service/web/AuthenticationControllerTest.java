package com.example.timestamp_service.web;

import com.example.timestamp_service.model.User;
import com.example.timestamp_service.model.exceptions.DuplicateEmailException;
import com.example.timestamp_service.model.exceptions.DuplicateUsernameException;
import com.example.timestamp_service.model.exceptions.UserNotFoundException;
import com.example.timestamp_service.service.UserService;
import com.example.timestamp_service.web.controller.AuthenticationController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testViewHomePage() {
        String view = authenticationController.viewHomePage();
        assertEquals("login", view);
    }

    @Test
    public void testGetSignup() {
        String view = authenticationController.getSignup();
        assertEquals("signup", view);
    }

    @Test
    public void testSignup() throws DuplicateEmailException, DuplicateUsernameException {
        String email = "test@domain.com";
        String fullname = "Test User";
        String username = "testuser";
        String password = "password";

        User user = new User(email, fullname, username, password, null);

        when(userService.saveUser(email, fullname, username, password)).thenReturn(user);

        String view = authenticationController.signup(email, fullname, username, password, model);

        assertEquals("redirect:/verify_email", view);
        verify(userService).saveUser(email, fullname, username, password);
        verify(userService).sendVerificationCode(user);
    }

    @Test
    public void testSignupWithDuplicateEmail() throws DuplicateEmailException, DuplicateUsernameException {
        String email = "test@domain.com";
        String fullname = "Test User";
        String username = "testuser";
        String password = "password";

        doThrow(new DuplicateEmailException()).when(userService).saveUser(email, fullname, username, password);

        String view = authenticationController.signup(email, fullname, username, password, model);

        assertEquals("signup", view);
        verify(model).addAttribute("duplicateEmail", true);
    }

    @Test
    public void testSignupWithDuplicateUsername() throws DuplicateEmailException, DuplicateUsernameException {
        String email = "test@domain.com";
        String fullname = "Test User";
        String username = "testuser";
        String password = "password";

        doThrow(new DuplicateUsernameException()).when(userService).saveUser(email, fullname, username, password);

        String view = authenticationController.signup(email, fullname, username, password, model);

        assertEquals("signup", view);
        verify(model).addAttribute("duplicateUsername", true);
    }

    @Test
    public void testVerifyEmailWithValidCode() {
        String code = "validCode";
        User user = new User();
        when(userService.findByCode(code)).thenReturn(user);

        String view = authenticationController.verifyEmailWithCode(code);

        assertEquals("redirect:/login", view);
        verify(userService).setIsValidated(user);
    }

    @Test
    public void testVerifyEmailWithInvalidCode() {
        String code = "invalidCode";
        when(userService.findByCode(code)).thenThrow(new UserNotFoundException());

        String view = authenticationController.verifyEmailWithCode(code);

        assertEquals("redirect:/verify_email", view);
    }

    @Test
    public void testSendPasswordResetToken() {
        String username = "testuser";

        String view = authenticationController.sendPasswordResetToken(username);

        assertEquals("redirect:/passwordReset", view);
        verify(userService).sendPasswordResetToken(username);
    }

    @Test
    public void testPasswordResetWithValidToken() {
        String username = "testuser";
        String token = "validToken";
        String password = "newPassword";

        when(userService.validateUsernameAndPasswordResetToken(username, token)).thenReturn(true);

        String view = authenticationController.passwordReset(username, token, password, model);

        assertEquals("login", view);
        verify(userService).changePassword(username, password);
        verify(userService).cleanPasswordToken(username);
    }

    @Test
    public void testPasswordResetWithInvalidToken() {
        String username = "testuser";
        String token = "invalidToken";
        String password = "newPassword";

        when(userService.validateUsernameAndPasswordResetToken(username, token)).thenReturn(false);

        String view = authenticationController.passwordReset(username, token, password, model);

        assertEquals("passwordReset", view);
        verify(model).addAttribute("invalidToken", "invalid");
    }
}
