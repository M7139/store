package com.ga.store.controller;

import com.ga.store.dto.ChangePasswordRequest;
import com.ga.store.dto.UpdateProfileRequest;
import com.ga.store.dto.UserResponse;
import com.ga.store.model.User;
import com.ga.store.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userService.getUserByEmail(email);

        UserResponse response = createUserResponse(user);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {

        String email = authentication.getName();

        User user = userService.updateProfile(
                email,
                request
        );

        UserResponse response = createUserResponse(user);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PatchMapping("/me/password")
    public ResponseEntity<String> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        String email = authentication.getName();

        userService.changePassword(email, request);

        return new ResponseEntity<>(
                "Password changed successfully",
                HttpStatus.OK
        );
    }

    private UserResponse createUserResponse(User user) {

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