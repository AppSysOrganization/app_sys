package com.appointmentsystem.model;

/**
 * Represents a generic user in the Appointment Scheduling System.
 * This class serves as a base for Admin, Supplier, and Customer.
 * 
 * @author Shahd
 * @version 1.0
 */
public abstract class User {

    /** The unique identifier for the user. */
    private int id;

    /** The username for login. */
    private String username;

    /** The password for login. */
    private String password;

    /** The email address of the user. */
    private String email;

    /**
     * Constructs a new User with the specified details.
     *
     * @param id       the unique identifier for the user
     * @param username the username for login
     * @param password the password for login
     * @param email    the email address of the user
     */
    public User(int id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    /**
     * Constructs a new User with default values.
     */
    public User() {
    }

    /**
     * Retrieves the user ID.
     *
     * @return the user ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the user ID.
     *
     * @param id the new user ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retrieves the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retrieves the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Retrieves the email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address.
     *
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }
}