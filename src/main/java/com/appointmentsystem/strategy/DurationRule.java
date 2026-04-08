package com.appointmentsystem.strategy;

import com.appointmentsystem.model.Appointment;
import java.time.Duration;

/**
 * A validation strategy that enforces a maximum duration for appointments.
 * 
 * @author Rahaf
 * @version 1.0
 */
public class DurationRule implements BookingRuleStrategy {

    /** The maximum allowed duration in minutes. */
    private int maxDurationMinutes;

    /**
     * Constructs a DurationRule with a specified maximum duration.
     *
     * @param maxDurationMinutes the maximum allowed duration in minutes
     */
    public DurationRule(int maxDurationMinutes) {
        this.maxDurationMinutes = maxDurationMinutes;
    }

    /**
     * Validates that the appointment duration does not exceed the limit.
     *
     * @param appointment the appointment to check
     * @return true if duration is within limits, false otherwise
     */
    @Override
    public boolean isValid(Appointment appointment) {
        if (appointment == null || appointment.getStartTime() == null || appointment.getEndTime() == null) {
            return false;
        }
        
        long duration = Duration.between(appointment.getStartTime(), appointment.getEndTime()).toMinutes();
        
        return duration > 0 && duration <= maxDurationMinutes;
    }
}