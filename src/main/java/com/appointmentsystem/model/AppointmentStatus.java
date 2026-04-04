package com.appointmentsystem.model;

/**
 * Represents the possible statuses of an appointment.
 * 
 * @author Shahd.
 * @version 1.1
 */
public enum AppointmentStatus {

    /** The appointment is proposed by a Supplier but not yet approved. */
    PENDING,

    /** The appointment is approved by Admin and available for booking. */
    APPROVED,

    /** The appointment has been booked by a Customer. */
    BOOKED,

    /** The appointment was cancelled by Admin or Customer. */
    CANCELLED,
    
    /** The appointment was rejected by Admin. */
    REJECTED,

    /** The appointment time has passed and it is no longer active. */
    COMPLETED,
    
    /** The appointment time has passed without anyone booking it. */
    EXPIRED
}