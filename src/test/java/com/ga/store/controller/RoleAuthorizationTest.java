package com.ga.store.controller;

import com.ga.store.service.AuditLogService;
import com.ga.store.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = RoleAuthorizationTest.TestConfig.class
)
public class RoleAuthorizationTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Configuration
    @EnableMethodSecurity
    static class TestConfig {

        @Bean
        UserService userService() {
            return mock(UserService.class);
        }

        @Bean
        AuditLogService auditLogService() {
            return mock(AuditLogService.class);
        }

        @Bean
        UserController userController(
                UserService userService,
                AuditLogService auditLogService) {

            return new UserController(
                    userService,
                    auditLogService
            );
        }
    }

    @Test
    @WithMockUser(
            username = "customer@test.com",
            roles = "CUSTOMER"
    )
    void customerShouldNotAccessAdminEndpoint() {

        assertThrows(
                AccessDeniedException.class,
                () -> userController.getAllUsers()
        );
    }

    @Test
    @WithMockUser(
            username = "admin@test.com",
            roles = "ADMIN"
    )
    void adminShouldAccessAdminEndpoint() {

        when(userService.getAllUsers())
                .thenReturn(
                        List.of()
                );

        var response =
                userController.getAllUsers();

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertEquals(
                0,
                response.getBody().size()
        );
    }
}