package com.appointmentsystem.strategy;

import com.appointmentsystem.model.Appointment;
import com.appointmentsystem.model.AppointmentType;

/**
 * A validation strategy that applies specific rules based on appointment type.
 * 
 * @author Team
 * @version 1.0
 */
public class TypeSpecificRule implements BookingRuleStrategy {

    /**
     * Validates type-specific constraints.
     *
     * @param appointment the appointment to check
     * @return true if valid according to type rules
     */
    @Override
    public boolean isValid(Appointment appointment) {
        if (appointment == null || appointment.getType() == null) {
            return false;
        }

        long duration = java.time.Duration.between(appointment.getStartTime(), appointment.getEndTime()).toMinutes();
        int participants = appointment.getMaxParticipants();

        switch (appointment.getType()) {
            case URGENT:
                return duration <= 30;
            case FOLLOW_UP:
                return participants == 1 && duration <= 30;
            case ASSESSMENT:
                return participants == 1 && duration >= 30;
            case VIRTUAL:
                return duration <= 60;
            case IN_PERSON:
                return duration <= 120;
            case INDIVIDUAL:
                return participants == 1;
            case GROUP:
                return participants > 1;
            default:
                return true;
        }
    }
}