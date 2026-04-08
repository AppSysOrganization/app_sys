package com.appointmentsystem.strategy;

import com.appointmentsystem.model.Appointment;
import com.appointmentsystem.model.AppointmentType;
import com.appointmentsystem.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DurationRule strategy class.
 *
 * @author Rahaf
 * @version 1.0
 */
class DurationRuleTest {

    /** The DurationRule instance configured with a maximum limit. */
    private DurationRule rule;
    
    /** A dummy Supplier object used for creating test appointments. */
    private Supplier supplier;

    @BeforeEach
    void setUp() {
        rule = new DurationRule(120);
        supplier = new Supplier(1, "Dr. Sam", "pass", "sam@test.com", "General");
    }

    @Test
    void testValidDuration() {
        LocalDateTime start = LocalDateTime.of(2023, 10, 10, 10, 0);
        LocalDateTime end = start.plusMinutes(60);
        
        Appointment appt = new Appointment(1, start, end, AppointmentType.IN_PERSON, 1, supplier);
        assertTrue(rule.isValid(appt));
    }

    @Test
    void testExceededDuration() {
        LocalDateTime start = LocalDateTime.of(2023, 10, 10, 10, 0);
        LocalDateTime end = start.plusMinutes(150);
        
        Appointment appt = new Appointment(1, start, end, AppointmentType.IN_PERSON, 1, supplier);
        assertFalse(rule.isValid(appt));
    }
}