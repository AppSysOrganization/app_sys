package com.appointmentsystem.model;

/**
 * Represents the different types of appointments available.
 * 
 * @author Elham..
 * @version 1.0
 */
public enum AppointmentType {
    /**
     * Urgent appointment.
     */
    URGENT,

    /**
     * Follow-up appointment.
     */
    FOLLOW_UP,

    /**
     * Assessment or evaluation appointment.
     */
    ASSESSMENT,

    /**
     * Virtual/Online appointment.
     */
    VIRTUAL,

    /**
     * In-person physical appointment.
     */
    IN_PERSON,

    /**
     * Individual appointment (one-on-one).
     */
    INDIVIDUAL,

    /**
     * Group appointment (multiple participants).
     */
    GROUP
}
