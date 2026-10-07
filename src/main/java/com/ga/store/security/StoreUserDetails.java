package com.ga.store.security;

import com.ga.store.enums.UserStatus;
import com.ga.store.model.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

/**
 * Represents an authenticated store user in Spring Security.
 * Includes the current token version for JWT invalidation.
 */
public class StoreUserDetails
        extends org.springframework.security.core.userdetails.User {

    private final long tokenVersion;

    /**
     * Converts a store user into Spring Security user details.
     * Only active, verified accounts are enabled.
     *
     * @param user store user
     */
    public StoreUserDetails(User user) {

        super(
                user.getEmail(),
                user.getPasswordHash(),
                user.getStatus() == UserStatus.ACTIVE
                        && user.isVerified(),
                true,
                true,
                true,
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().name()
                        )
                )
        );

        tokenVersion = user.getTokenVersion();
    }

    /**
     * Returns the account's current token version.
     *
     * @return current token version
     */
    public long getTokenVersion() {
        return tokenVersion;
    }
}