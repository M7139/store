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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Authentication",
        description = "User registration, login, email verification and password recovery"
)
@SecurityRequirements
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
    @Operation(
            summary = "Register a new customer",
            description = "Creates a new customer account and sends an email verification link."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid registration information"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists"
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many registration requests"
            )
    })
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
    @Operation(
            summary = "Login",
            description = "Authenticates a user and returns a JWT token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request information"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid email or password"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Account is inactive or email is not verified"
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many login requests"
            )
    })
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
    @Operation(
            summary = "Verify email",
            description = "Verifies a user's email address using the verification token sent by email."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Email verified successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Verification token is invalid or expired"
            )
    })
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
    @Operation(
            summary = "Request password reset",
            description = "Creates a password reset request and sends a reset link to the user's email."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset request created"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request information"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many password reset requests"
            )
    })
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
    @Operation(
            summary = "Reset password",
            description = "Resets a user's password using a valid password reset token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Reset token is invalid or expired"
            )
    })
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