package com.ga.store.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(
            JwtUtils jwtUtils,
            CustomUserDetailsService customUserDetailsService) {

        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        if (!jwtUtils.validateToken(token)) {

            filterChain.doFilter(request, response);
            return;
        }

        try {

            String email = jwtUtils.extractEmail(token);

            if (email != null
                    && SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                UserDetails userDetails =
                        customUserDetailsService
                                .loadUserByUsername(email);

                if (userDetails instanceof StoreUserDetails storeUserDetails
                        && userDetails.isEnabled()
                        && jwtUtils.isTokenCurrent(
                        token,
                        storeUserDetails.getTokenVersion()
                )) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (UsernameNotFoundException
                 | JwtException
                 | IllegalArgumentException exception) {

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}