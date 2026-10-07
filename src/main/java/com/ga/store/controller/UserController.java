package com.ga.store.controller;

import com.ga.store.dto.ChangePasswordRequest;
import com.ga.store.dto.UpdateProfileRequest;
import com.ga.store.dto.UserResponse;
import com.ga.store.dto.UserStatusRequest;
import com.ga.store.model.User;
import com.ga.store.service.AuditLogService;
import com.ga.store.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(
        name = "Users",
        description = "User profile and admin user management"
)
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final AuditLogService auditLogService;

    public UserController(
            UserService userService,
            AuditLogService auditLogService) {

        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get current user",
            description = "Returns the profile of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    public ResponseEntity<UserResponse> getCurrentUser(
            Authentication authentication) {

        String email = authentication.getName();

        User user =
                userService.getUserByEmail(email);

        UserResponse response =
                createUserResponse(user);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PutMapping("/me")
    @Operation(
            summary = "Update current user profile",
            description = "Updates the profile information of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid profile information"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    public ResponseEntity<UserResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {

        String email = authentication.getName();

        User user = userService.updateProfile(
                email,
                request
        );

        UserResponse response =
                createUserResponse(user);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PostMapping(
            value = "/me/profile-picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Upload profile picture",
            description = "Uploads or replaces the profile picture of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile picture uploaded successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid file"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    public ResponseEntity<UserResponse> uploadProfilePicture(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {

        String email = authentication.getName();

        User user =
                userService.uploadProfilePicture(
                        email,
                        file
                );

        UserResponse response =
                createUserResponse(user);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PatchMapping("/me/password")
    @Operation(
            summary = "Change password",
            description = "Changes the password of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password changed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid password information"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required or current password is incorrect"
            )
    })
    public ResponseEntity<String> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        String email = authentication.getName();

        userService.changePassword(
                email,
                request
        );

        return new ResponseEntity<>(
                "Password changed successfully",
                HttpStatus.OK
        );
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all users",
            description = "Returns all registered users. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            )
    })
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> response =
                userService.getAllUsers()
                        .stream()
                        .map(this::createUserResponse)
                        .toList();

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @GetMapping("/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get user by ID",
            description = "Returns a specific user by ID. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long userId) {

        User user =
                userService.getUserById(
                        userId
                );

        UserResponse response =
                createUserResponse(user);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PatchMapping("/admin/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update user status",
            description = "Changes a user's status between ACTIVE and INACTIVE. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user status"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    public ResponseEntity<UserResponse> updateUserStatus(
            Authentication authentication,
            @PathVariable Long userId,
            @Valid @RequestBody UserStatusRequest request) {

        User admin =
                userService.getUserByEmail(
                        authentication.getName()
                );

        User user =
                userService.updateUserStatus(
                        userId,
                        request
                );

        auditLogService.createAuditLog(
                admin.getId(),
                "USER_STATUS_CHANGED",
                "Changed user "
                        + user.getId()
                        + " status to "
                        + user.getStatus()
        );

        UserResponse response =
                createUserResponse(user);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    private UserResponse createUserResponse(
            User user) {

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.isVerified(),
                user.getProfilePictureUrl()
        );
    }
}