package com.ga.store.service;

import com.ga.store.dto.LoginRequest;
import com.ga.store.enums.UserStatus;
import com.ga.store.exception.EmailNotVerifiedException;
import com.ga.store.exception.InactiveAccountException;
import com.ga.store.exception.InvalidCredentialsException;
import com.ga.store.model.User;
import com.ga.store.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private ImageStorageService imageStorageService;

    private UserService userService;

    @BeforeEach
    void setUp() {

        userService = new UserService(
                userRepository,
                passwordEncoder,
                emailVerificationService,
                imageStorageService
        );
    }

    @Test
    void loginUserShouldReturnUserWhenCredentialsAreCorrect() {

        LoginRequest request =
                new LoginRequest(
                        "customer@test.com",
                        "Password123!"
                );

        User user =
                new User(
                        "Test",
                        "Customer",
                        "customer@test.com",
                        "hashedPassword"
                );

        user.setId(1L);
        user.setStatus(UserStatus.ACTIVE);
        user.setVerified(true);

        when(userRepository.findByEmail(
                "customer@test.com"
        )).thenReturn(
                Optional.of(user)
        );

        when(passwordEncoder.matches(
                "Password123!",
                "hashedPassword"
        )).thenReturn(true);

        User result =
                userService.loginUser(
                        request
                );

        assertNotNull(result);
        assertEquals(
                "customer@test.com",
                result.getEmail()
        );
    }

    @Test
    void loginUserShouldFailWhenPasswordIsIncorrect() {

        LoginRequest request =
                new LoginRequest(
                        "customer@test.com",
                        "WrongPassword"
                );

        User user =
                new User(
                        "Test",
                        "Customer",
                        "customer@test.com",
                        "hashedPassword"
                );

        user.setId(1L);
        user.setStatus(UserStatus.ACTIVE);
        user.setVerified(true);

        when(userRepository.findByEmail(
                "customer@test.com"
        )).thenReturn(
                Optional.of(user)
        );

        when(passwordEncoder.matches(
                "WrongPassword",
                "hashedPassword"
        )).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(
                        request
                )
        );
    }

    @Test
    void loginUserShouldFailWhenAccountIsInactive() {

        LoginRequest request =
                new LoginRequest(
                        "customer@test.com",
                        "Password123!"
                );

        User user =
                new User(
                        "Test",
                        "Customer",
                        "customer@test.com",
                        "hashedPassword"
                );

        user.setId(1L);
        user.setStatus(UserStatus.INACTIVE);
        user.setVerified(true);

        when(userRepository.findByEmail(
                "customer@test.com"
        )).thenReturn(
                Optional.of(user)
        );

        when(passwordEncoder.matches(
                "Password123!",
                "hashedPassword"
        )).thenReturn(true);

        assertThrows(
                InactiveAccountException.class,
                () -> userService.loginUser(
                        request
                )
        );
    }

    @Test
    void loginUserShouldFailWhenEmailIsNotVerified() {

        LoginRequest request =
                new LoginRequest(
                        "customer@test.com",
                        "Password123!"
                );

        User user =
                new User(
                        "Test",
                        "Customer",
                        "customer@test.com",
                        "hashedPassword"
                );

        user.setId(1L);
        user.setStatus(UserStatus.ACTIVE);
        user.setVerified(false);

        when(userRepository.findByEmail(
                "customer@test.com"
        )).thenReturn(
                Optional.of(user)
        );

        when(passwordEncoder.matches(
                "Password123!",
                "hashedPassword"
        )).thenReturn(true);

        assertThrows(
                EmailNotVerifiedException.class,
                () -> userService.loginUser(
                        request
                )
        );
    }
}