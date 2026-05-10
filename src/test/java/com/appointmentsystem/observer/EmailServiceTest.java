package com.appointmentsystem.observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EmailService.
 *
 * @author Elham.
 * @version 1.0
 */
class EmailServiceTest {

    /** The EmailService instance used for testing. */
    private EmailService service;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        service = new EmailService();
    }

    /**
     * Tests that EmailService constructs without throwing exceptions.
     */
    @Test
    void testEmailServiceConstructor() {
    	assertDoesNotThrow(EmailService::new);
    }

    /**
     * Tests that update handles null message without throwing.
     */
    @Test
    void testUpdateWithNullMessage() {
        assertDoesNotThrow(() -> service.update(null));
    }

    /**
     * Tests that update handles empty message without throwing.
     */
    @Test
    void testUpdateWithEmptyMessage() {
        assertDoesNotThrow(() -> service.update(""));
    }

    /**
     * Tests that update handles blank message without throwing.
     */
    @Test
    void testUpdateWithBlankMessage() {
        assertDoesNotThrow(() -> service.update("   "));
    }

    /**
     * Tests that update handles message with one part only without throwing.
     */
    @Test
    void testUpdateWithSinglePartMessage() {
        assertDoesNotThrow(() -> service.update("onlyOneWord"));
    }
    
    /**
     * Tests that update sends email when credentials and message are valid.
     */
    @Test
    void testUpdateWithValidEmailFormat() {
        assertDoesNotThrow(() -> service.update("test@example.com This is a test body"));
    }

    /**
     * Tests that update handles message with no space (single part).
     */
    @Test  
    void testUpdateWithNoBodyPart() {
        assertDoesNotThrow(() -> service.update("test@example.com"));
    }
}