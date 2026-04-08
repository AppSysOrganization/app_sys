package com.appointmentsystem.strategy;

import com.appointmentsystem.model.Appointment;

/**
 * Strategy interface for validating appointment booking rules.
 * 
 * @author Shahd
 * @version 1.0
 */
public interface BookingRuleStrategy {

    /**
     * Checks if an appointment is valid according to the specific rule.
     *
     * @param appointment the appointment to validate
     * @return true if the appointment is valid, false otherwise
     */
    boolean isValid(Appointment appointment);
}