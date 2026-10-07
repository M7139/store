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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Handles user-related business logic including registration,
 * authentication, profile management, password changes,
 * profile pictures and account status management.
 */
@Service
public class UserService {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    UserService.class
            );

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

    /**
     * Returns all registered users.
     *
     * @return list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Finds a user by their ID.
     *
     * @param id user ID
     * @return matching user
     * @throws InformationNotFoundException if the user does not exist
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "User with id " + id + " not found"
                        ));
    }

    /**
     * Finds a user by their email address.
     *
     * @param email user email address
     * @return matching user
     * @throws InformationNotFoundException if the user does not exist
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "User with this email not found"
                        ));
    }

    /**
     * Checks whether a user account already exists for an email address.
     *
     * @param email email address to check
     * @return true if the email already exists
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Registers a new customer account.
     * The password is hashed before storage and an email
     * verification token is created for the new user.
     *
     * @param request registration information
     * @return newly created user
     * @throws InformationExistsException if the email is already registered
     */
    public User registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {

            logger.warn(
                    "Registration failed because email already exists"
            );

            throw new InformationExistsException(
                    "User with this email already exists"
            );
        }

        String passwordHash =
                passwordEncoder.encode(
                        request.getPassword()
                );

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

        logger.info(
                "User registered successfully with id {}",
                savedUser.getId()
        );

        return savedUser;
    }

    /**
     * Authenticates a user using their email and password.
     * Inactive and unverified users are prevented from logging in.
     *
     * @param request login credentials
     * @return authenticated user
     * @throws InvalidCredentialsException if the credentials are incorrect
     * @throws InactiveAccountException if the account is inactive
     * @throws EmailNotVerifiedException if the email has not been verified
     */
    public User loginUser(LoginRequest request) {

        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() -> {

                            logger.warn(
                                    "Login failed because user was not found"
                            );

                            return new InvalidCredentialsException(
                                    "Invalid email or password"
                            );
                        });

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {

            logger.warn(
                    "Login failed for user id {} because password was incorrect",
                    user.getId()
            );

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        if (user.getStatus()
                .name()
                .equals("INACTIVE")) {

            logger.warn(
                    "Login blocked for inactive user id {}",
                    user.getId()
            );

            throw new InactiveAccountException(
                    "Account is inactive"
            );
        }

        if (!user.isVerified()) {

            logger.warn(
                    "Login blocked for unverified user id {}",
                    user.getId()
            );

            throw new EmailNotVerifiedException(
                    "Email is not verified"
            );
        }

        logger.info(
                "User logged in successfully with id {}",
                user.getId()
        );

        return user;
    }

    /**
     * Changes the password of the currently authenticated user.
     * The current password must be correct before the new
     * password is accepted.
     *
     * @param email authenticated user's email
     * @param request current and new password information
     * @throws InvalidCredentialsException if the current password is incorrect
     */
    public void changePassword(
            String email,
            ChangePasswordRequest request) {

        User user =
                getUserByEmail(email);

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPasswordHash())) {

            logger.warn(
                    "Password change failed for user id {}",
                    user.getId()
            );

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

        logger.info(
                "Password changed successfully for user id {}",
                user.getId()
        );
    }

    /**
     * Updates the first and last name of the currently
     * authenticated user.
     *
     * @param email authenticated user's email
     * @param request updated profile information
     * @return updated user
     */
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

        User savedUser =
                userRepository.save(user);

        logger.info(
                "Profile updated for user id {}",
                savedUser.getId()
        );

        return savedUser;
    }

    /**
     * Uploads or replaces the profile picture of the
     * currently authenticated user.
     *
     * @param email authenticated user's email
     * @param file image file to upload
     * @return updated user
     */
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

        logger.info(
                "Profile picture updated for user id {}",
                savedUser.getId()
        );

        return savedUser;
    }

    /**
     * Updates a user's account status.
     * This operation is used by administrators to activate
     * or deactivate user accounts.
     *
     * @param userId ID of the user being updated
     * @param request new user status
     * @return updated user
     */
    public User updateUserStatus(
            Long userId,
            UserStatusRequest request) {

        User user =
                getUserById(userId);

        user.setStatus(
                request.getStatus()
        );

        User savedUser =
                userRepository.save(user);

        logger.info(
                "User id {} status changed to {}",
                savedUser.getId(),
                savedUser.getStatus()
        );

        return savedUser;
    }
}