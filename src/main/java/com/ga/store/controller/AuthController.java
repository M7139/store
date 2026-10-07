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

/**
 * Provides registration, login, email verification
 * and password recovery endpoints.
 */
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

    /**
     * Registers a customer and schedules verification email delivery.
     *
     * @param httpRequest HTTP request used for rate limiting
     * @param request registration information
     * @return created user
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register a new customer",
            description = "Creates a new customer account and schedules an email verification link."
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

        String ipAddress = httpRequest.getRemoteAddr();

        rateLimitService.checkLimit(
                "register:" + ipAddress,
                3,
                10
        );

        User user = userService.registerUser(request);

        UserResponse response = new UserResponse(
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

    /**
     * Authenticates a user and issues a JWT containing
     * the token version from the authenticated account.
     *
     * @param httpRequest HTTP request used for rate limiting
     * @param request login credentials
     * @return JWT and user information
     */
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

        String ipAddress = httpRequest.getRemoteAddr();

        rateLimitService.checkLimit(
                "login:" + ipAddress,
                5,
                1
        );

        User user = userService.loginUser(request);

        String token = jwtUtils.generateToken(
                user.getEmail(),
                user.getTokenVersion()
        );

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.isVerified(),
                user.getProfilePictureUrl()
        );

        LoginResponse response = new LoginResponse(
                token,
                userResponse
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    /**
     * Verifies an account using its current verification token.
     *
     * @param token verification token
     * @return verification confirmation
     */
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
                    description = "Verification token has expired"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Verification token not found"
            )
    })
    public ResponseEntity<String> verifyEmail(
            @RequestParam String token) {

        emailVerificationService.verifyEmail(token);

        return new ResponseEntity<>(
                "Email verified successfully",
                HttpStatus.OK
        );
    }

    /**
     * Requests a replacement email verification link.
     * Limits requests by both IP address and account email.
     * The response does not reveal whether the account exists.
     *
     * @param httpRequest HTTP request used for rate limiting
     * @param request validated email information
     * @return generic request confirmation
     */
    @PostMapping("/resend-verification")
    @Operation(
            summary = "Resend email verification",
            description = "Schedules a replacement verification email for an unverified account."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Verification email request accepted"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid email information"
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many verification email requests"
            )
    })
    public ResponseEntity<String> resendVerification(
            HttpServletRequest httpRequest,
            @Valid @RequestBody ForgotPasswordRequest request) {

        String ipAddress = httpRequest.getRemoteAddr();

        rateLimitService.checkLimit(
                "resend-verification:" + ipAddress,
                3,
                10
        );

        rateLimitService.checkLimit(
                "resend-verification-email:" + request.getEmail(),
                3,
                10
        );

        emailVerificationService.resendVerification(
                request.getEmail()
        );

        return ResponseEntity.ok(
                "If the account needs verification, an email will be sent"
        );
    }

    /**
     * Requests a password reset email.
     * The response does not reveal whether the account exists.
     *
     * @param httpRequest HTTP request used for rate limiting
     * @param request account email
     * @return generic request confirmation
     */
    @PostMapping("/forgot-password")
    @Operation(
            summary = "Request password reset",
            description = "Schedules a password reset email when an account exists for the supplied email."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset request accepted"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request information"
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many password reset requests"
            )
    })
    public ResponseEntity<String> forgotPassword(
            HttpServletRequest httpRequest,
            @Valid @RequestBody ForgotPasswordRequest request) {

        String ipAddress = httpRequest.getRemoteAddr();

        rateLimitService.checkLimit(
                "forgot-password:" + ipAddress,
                3,
                10
        );

        rateLimitService.checkLimit(
                "forgot-password-email:" + request.getEmail(),
                3,
                10
        );

        passwordResetService.requestPasswordReset(
                request.getEmail()
        );

        return ResponseEntity.ok(
                "If an account exists with this email, a password reset email will be sent"
        );
    }

    /**
     * Resets a password and invalidates previously issued JWTs.
     *
     * @param request reset token and new password
     * @return reset confirmation
     */
    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset password",
            description = "Resets a user's password using a valid password reset token and invalidates existing JWTs."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request is invalid or reset token has expired"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reset token not found"
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