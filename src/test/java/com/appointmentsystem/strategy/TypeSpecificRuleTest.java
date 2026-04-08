package com.appointmentsystem.strategy;

import com.appointmentsystem.model.Appointment;
import com.appointmentsystem.model.AppointmentType;
import com.appointmentsystem.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TypeSpecificRule strategy.
 * 
 * @author Team
 * @version 1.0
 */
class TypeSpecificRuleTest {

    /** The rule instance under test. */
    private TypeSpecificRule rule;
    
    /** The supplier used for testing. */
    private Supplier supplier;

    @BeforeEach
    void setUp() {
        rule = new TypeSpecificRule();
        supplier = new Supplier(1, "Dr. Sam", "pass", "sam@test.com", "General");
    }

    @Test
    void testUrgentValid() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(20);
        Appointment appt = new Appointment(1, start, end, AppointmentType.URGENT, 1, supplier);
        assertTrue(rule.isValid(appt));
    }

    @Test
    void testUrgentInvalidDuration() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(40);
        Appointment appt = new Appointment(1, start, end, AppointmentType.URGENT, 1, supplier);
        assertFalse(rule.isValid(appt));
    }

    @Test
    void testFollowUpValid() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(20);
        Appointment appt = new Appointment(1, start, end, AppointmentType.FOLLOW_UP, 1, supplier);
        assertTrue(rule.isValid(appt));
    }

    @Test
    void testFollowUpInvalidDuration() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(40);
        Appointment appt = new Appointment(1, start, end, AppointmentType.FOLLOW_UP, 1, supplier);
        assertFalse(rule.isValid(appt));
    }

    @Test
    void testFollowUpInvalidParticipants() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(20);
        Appointment appt = new Appointment(1, start, end, AppointmentType.FOLLOW_UP, 2, supplier);
        assertFalse(rule.isValid(appt));
    }

    @Test
    void testAssessmentValid() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(40);
        Appointment appt = new Appointment(1, start, end, AppointmentType.ASSESSMENT, 1, supplier);
        assertTrue(rule.isValid(appt));
    }

    @Test
    void testAssessmentInvalidDuration() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(20);
        Appointment appt = new Appointment(1, start, end, AppointmentType.ASSESSMENT, 1, supplier);
        assertFalse(rule.isValid(appt));
    }
    
    @Test
    void testAssessmentInvalidParticipants() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(40);
        Appointment appt = new Appointment(1, start, end, AppointmentType.ASSESSMENT, 2, supplier);
        assertFalse(rule.isValid(appt));
    }

    @Test
    void testVirtualValid() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(50);
        Appointment appt = new Appointment(1, start, end, AppointmentType.VIRTUAL, 1, supplier);
        assertTrue(rule.isValid(appt));
    }

    @Test
    void testVirtualInvalidDuration() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(70);
        Appointment appt = new Appointment(1, start, end, AppointmentType.VIRTUAL, 1, supplier);
        assertFalse(rule.isValid(appt));
    }

    @Test
    void testInPersonValid() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(90);
        Appointment appt = new Appointment(1, start, end, AppointmentType.IN_PERSON, 1, supplier);
        assertTrue(rule.isValid(appt));
    }

    @Test
    void testInPersonInvalidDuration() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(130);
        Appointment appt = new Appointment(1, start, end, AppointmentType.IN_PERSON, 1, supplier);
        assertFalse(rule.isValid(appt));
    }

    @Test
    void testIndividualValid() {
        Appointment appt = new Appointment(1, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), AppointmentType.INDIVIDUAL, 1, supplier);
        assertTrue(rule.isValid(appt));
    }

    @Test
    void testIndividualInvalidParticipants() {
        Appointment appt = new Appointment(1, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), AppointmentType.INDIVIDUAL, 2, supplier);
        assertFalse(rule.isValid(appt));
    }

    @Test
    void testGroupValid() {
        Appointment appt = new Appointment(1, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), AppointmentType.GROUP, 5, supplier);
        assertTrue(rule.isValid(appt));
    }

    @Test
    void testGroupInvalidParticipants() {
        Appointment appt = new Appointment(1, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), AppointmentType.GROUP, 1, supplier);
        assertFalse(rule.isValid(appt));
    }
    
    @Test
    void testNullAppointment() {
        assertFalse(rule.isValid(null));
    }

    @Test
    void testNullType() {
        Appointment appt = new Appointment(1, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), null, 1, supplier);
        assertFalse(rule.isValid(appt));
    }
}