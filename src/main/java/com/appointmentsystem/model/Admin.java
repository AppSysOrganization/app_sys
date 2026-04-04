package com.appointmentsystem.model;

/**
 * Represents an Administrator in the system.
 * Administrators manage schedules and approve appointments.
 * 
 * @author Elham
 * @version 1.0
 */
public class Admin extends User {

    /**
     * Constructs a new Admin.
     *
     * @param id       the unique identifier
     * @param username the admin username
     * @param password the admin password
     * @param email    the admin email
     */
    public Admin(int id, String username, String password, String email) {
        super(id, username, password, email);
    }
}