package com.appointmentsystem.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Admin class.
 * Validates the instantiation and basic properties of the Admin model.
 *
 * @author Elham....
 * @version 1.0
 */
class AdminTest {

    /** The Admin instance used as a test fixture. */
    private Admin admin;

    /**
     * Sets up the test environment before each test method.
     * Initializes a default Admin object for use in tests.
     */
    @BeforeEach
    void setUp() {
        admin = new Admin(1, "admin1", "adminPass", "admin@system.com");
    }

    /**
     * Tests the Admin constructor.
     * Ensures that the ID and username are correctly set upon object creation.
     */
    @Test
    void testAdminConstructor() {
        assertEquals(1, admin.getId());
        assertEquals("admin1", admin.getUsername());
    }
}