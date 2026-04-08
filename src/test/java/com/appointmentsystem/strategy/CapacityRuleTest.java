package com.appointmentsystem.strategy;

import com.appointmentsystem.model.Appointment;

/**
 * A validation strategy that enforces a maximum number of participants.
 * 
 * @author Elham
 * @version 1.0
 */
public class CapacityRuleTest implements BookingRuleStrategy {

    /** The maximum number of participants allowed. */
    private int maxParticipants;

    /**
     * Constructs a CapacityRule with a specified limit.
     *
     * @param maxParticipants the maximum number of participants allowed
     */
    public CapacityRuleTest(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    /**
     * Validates that the appointment participant count does not exceed the limit.
     *
     * @param appointment the appointment to check
     * @return true if within capacity, false otherwise
     */
    @Override
    public boolean isValid(Appointment appointment) {
        if (appointment == null) {
            return false;
        }
        
        return appointment.getMaxParticipants() > 0 && 
               appointment.getMaxParticipants() <= maxParticipants;
    }
}