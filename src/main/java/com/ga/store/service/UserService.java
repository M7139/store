package com.ga.store.service;

import com.ga.store.dto.ChangePasswordRequest;
import com.ga.store.dto.LoginRequest;
import com.ga.store.dto.RegisterRequest;
import com.ga.store.dto.UpdateProfileRequest;
import com.ga.store.dto.UserStatusRequest;
import com.ga.store.exception.EmailNotVerifiedException;
import com.ga.store.exception.InactiveAccountException;
import com.ga.store.exception.InformationExistsException;
import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.exception.InvalidCredentialsException;
import com.ga.store.model.User;
import com.ga.store.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final ImageStorageService imageStorageService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailVerificationService emailVerificationService,
            ImageStorageService imageStorageService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
        this.imageStorageService = imageStorageService;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "User with id " + id + " not found"
                        ));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "User with this email not found"
                        ));
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public User registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InformationExistsException(
                    "User with this email already exists"
            );
        }

        String passwordHash =
                passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordHash
        );

        User savedUser =
                userRepository.save(user);

        emailVerificationService
                .createVerificationToken(savedUser);

        return savedUser;
    }

    public User loginUser(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        if (user.getStatus().name().equals("INACTIVE")) {
            throw new InactiveAccountException(
                    "Account is inactive"
            );
        }

        if (!user.isVerified()) {
            throw new EmailNotVerifiedException(
                    "Email is not verified"
            );
        }

        return user;
    }

    public void changePassword(
            String email,
            ChangePasswordRequest request) {

        User user = getUserByEmail(email);

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPasswordHash())) {

            throw new InvalidCredentialsException(
                    "Current password is incorrect"
            );
        }

        String newPasswordHash =
                passwordEncoder.encode(
                        request.getNewPassword()
                );

        user.setPasswordHash(
                newPasswordHash
        );

        userRepository.save(user);
    }

    public User updateProfile(
            String email,
            UpdateProfileRequest request) {

        User user =
                getUserByEmail(email);

        user.setFirstName(
                request.getFirstName().trim()
        );

        user.setLastName(
                request.getLastName().trim()
        );

        return userRepository.save(user);
    }

    public User uploadProfilePicture(
            String email,
            MultipartFile file) {

        User user =
                getUserByEmail(email);

        String oldProfilePicture =
                user.getProfilePictureUrl();

        String profilePictureUrl =
                imageStorageService
                        .saveProfileImage(file);

        user.setProfilePictureUrl(
                profilePictureUrl
        );

        User savedUser =
                userRepository.save(user);

        if (oldProfilePicture != null
                && !oldProfilePicture.isBlank()) {

            imageStorageService
                    .deleteProfileImage(
                            oldProfilePicture
                    );
        }

        return savedUser;
    }

    public User updateUserStatus(
            Long userId,
            UserStatusRequest request) {

        User user =
                getUserById(userId);

        user.setStatus(
                request.getStatus()
        );

        return userRepository.save(user);
    }
}