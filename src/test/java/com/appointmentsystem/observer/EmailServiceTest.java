package com.appointmentsystem.observer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EmailService.
 * 
 * @author Elham.
 * @version 1.0
 */
class EmailServiceTest {

    /**
     * Tests that the update method executes without throwing exceptions.
     */
    @Test
    void testUpdateDoesNotThrow() {
        EmailService service = new EmailService();
        assertDoesNotThrow(() -> service.update("Test Message"));
    }
}