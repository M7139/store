package com.ga.store.controller;

import com.ga.store.dto.ForgotPasswordRequest;
import com.ga.store.dto.LoginRequest;
import com.ga.store.dto.LoginResponse;
import com.ga.store.dto.RegisterRequest;
import com.ga.store.dto.ResetPasswordRequest;
import com.ga.store.dto.UserResponse;
import com.ga.store.model.User;
import com.ga.store.security.JwtUtils;
import com.ga.store.service.EmailVerificationService;
import com.ga.store.service.PasswordResetService;
import com.ga.store.service.RateLimitService;
import com.ga.store.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;
    private final RateLimitService rateLimitService;

    public AuthController(
            UserService userService,
            JwtUtils jwtUtils,
            EmailVerificationService emailVerificationService,
            PasswordResetService passwordResetService,
            RateLimitService rateLimitService) {

        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.emailVerificationService = emailVerificationService;
        this.passwordResetService = passwordResetService;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            HttpServletRequest httpRequest,
            @Valid @RequestBody RegisterRequest request) {

        String ipAddress =
                httpRequest.getRemoteAddr();

        rateLimitService.checkLimit(
                "register:" + ipAddress,
                3,
                10
        );

        User user =
                userService.registerUser(
                        request
                );

        UserResponse response =
                new UserResponse(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getStatus(),
                        user.isVerified(),
                        user.getProfilePictureUrl()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            HttpServletRequest httpRequest,
            @Valid @RequestBody LoginRequest request) {

        String ipAddress =
                httpRequest.getRemoteAddr();

        rateLimitService.checkLimit(
                "login:" + ipAddress,
                5,
                1
        );

        User user =
                userService.loginUser(
                        request
                );

        String token =
                jwtUtils.generateToken(
                        user.getEmail()
                );

        UserResponse userResponse =
                new UserResponse(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getStatus(),
                        user.isVerified(),
                        user.getProfilePictureUrl()
                );

        LoginResponse response =
                new LoginResponse(
                        token,
                        userResponse
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(
            @RequestParam String token) {

        emailVerificationService.verifyEmail(
                token
        );

        return new ResponseEntity<>(
                "Email verified successfully",
                HttpStatus.OK
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            HttpServletRequest httpRequest,
            @Valid @RequestBody ForgotPasswordRequest request) {

        String ipAddress =
                httpRequest.getRemoteAddr();

        rateLimitService.checkLimit(
                "forgot-password:" + ipAddress,
                3,
                10
        );

        passwordResetService.requestPasswordReset(
                request.getEmail()
        );

        return new ResponseEntity<>(
                "Password reset request created",
                HttpStatus.OK
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return new ResponseEntity<>(
                "Password reset successfully",
                HttpStatus.OK
        );
    }
}