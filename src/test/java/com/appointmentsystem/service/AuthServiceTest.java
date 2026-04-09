package com.appointmentsystem.service;

import com.appointmentsystem.model.Admin;
import com.appointmentsystem.model.User;
import com.appointmentsystem.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService class.
 * 
 * @author Shahd
 * @version 1.0
 */
class AuthServiceTest {

    /** Mock repository for simulating user data storage. */
    private InMemoryUserRepository mockRepo;
    
    /** The service instance to be tested. */
    private AuthService authService;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        mockRepo = mock(InMemoryUserRepository.class);
        authService = new AuthService(mockRepo);
    }

    /**
     * Tests successful administrator login with valid credentials.
     */
    @Test
    void testLoginSuccess() {
        User admin = new Admin(1, "admin", "1234", "admin@test.com");
        when(mockRepo.findByUsername("admin")).thenReturn(Optional.of(admin));

        User result = authService.login("admin", "1234");
        
        assertNotNull(result, "Login should succeed with valid credentials");
        assertEquals("admin", result.getUsername());
        assertEquals(result, authService.getCurrentUser(), "Current user should be set after login");
    }

    /**
     * Tests login failure with incorrect password.
     */
    @Test
    void testLoginFailWrongPassword() {
        User admin = new Admin(1, "admin", "1234", "admin@test.com");
        when(mockRepo.findByUsername("admin")).thenReturn(Optional.of(admin));

        User result = authService.login("admin", "wrongpass");
        
        assertNull(result, "Login should fail with wrong password");
        assertNull(authService.getCurrentUser(), "Current user should be null after failed login");
    }
    
    /**
     * Tests administrator logout functionality.
     */
    @Test
    void testLogout() {
        User admin = new Admin(1, "admin", "1234", "admin@test.com");
        when(mockRepo.findByUsername("admin")).thenReturn(Optional.of(admin));
        authService.login("admin", "1234");
        assertNotNull(authService.getCurrentUser(), "User should be logged in");

        authService.logout();

        assertNull(authService.getCurrentUser(), "Current user should be null after logout");
    }
}