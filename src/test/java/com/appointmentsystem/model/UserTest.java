package com.appointmentsystem.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the User class.
 * Tests constructor and getter/setter functionality.
 * 
 * @author Shahd
 * @version 1.0
 */
class UserTest {

    /** The user instance used for testing. */
    private User testUser;

    /**
     * Helper class to create a concrete User instance for testing
     * since User is abstract.
     * 
     * @author Shahd
     * @version 1.0
     */
    private static class TestUser extends User {

        /**
         * Constructs a new TestUser with the specified details.
         *
         * @param id       the unique identifier for the user
         * @param username the username for login
         * @param password the password for login
         * @param email    the email address of the user
         */
        public TestUser(int id, String username, String password, String email) {
            super(id, username, password, email);
        }
    }

    /**
     * Initializes test environment before all tests.
     */
    @BeforeAll
    static void initAll() {
        System.out.println("Starting User Tests...");
    }

    /**
     * Sets up a new user before each test.
     */
    @BeforeEach
    void init() {
        testUser = new TestUser(1, "testuser", "password123", "test@example.com");
    }

    /**
     * Cleans up resources after each test.
     */
    @AfterEach
    void tearDown() {
        testUser = null;
    }

    /**
     * Cleans up environment after all tests.
     */
    @AfterAll
    static void tearDownAll() {
        System.out.println("Finished User Tests.");
    }

    /**
     * Tests the user constructor and initial values.
     */
    @Test
    void testUserConstructor() {
        assertEquals(1, testUser.getId(), "ID should be 1");
        assertEquals("testuser", testUser.getUsername(), "Username mismatch");
        assertEquals("password123", testUser.getPassword(), "Password mismatch");
        assertEquals("test@example.com", testUser.getEmail(), "Email mismatch");
    }

    /**
     * Tests setting the username.
     */
    @Test
    void testSetUsername() {
        String newUsername = "newUser";
        testUser.setUsername(newUsername);
        assertEquals(newUsername, testUser.getUsername(), "Username not updated correctly");
    }

    /**
     * Tests setting the password.
     */
    @Test
    void testSetPassword() {
        String newPassword = "newPass";
        testUser.setPassword(newPassword);
        assertEquals(newPassword, testUser.getPassword(), "Password not updated correctly");
    }

    /**
     * Tests setting the email address.
     */
    @Test
    void testSetEmail() {
        String newEmail = "updated@example.com";
        testUser.setEmail(newEmail);
        assertEquals(newEmail, testUser.getEmail(), "Email not updated correctly");
    }
}