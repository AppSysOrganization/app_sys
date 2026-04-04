package com.appointmentsystem.model;

/**
 * Represents a Customer who books appointments.
 * 
 * @author Rahaf.
 * @version 1.0
 */
public class Customer extends User {

    /** The contact number of the customer. */
    private String phoneNumber;

    /**
     * Constructs a new Customer with default values.
     */
    public Customer() {
    }

    /**
     * Constructs a new Customer.
     *
     * @param id          the unique identifier
     * @param username    the customer username
     * @param password    the customer password
     * @param email       the customer email
     * @param phoneNumber the customer contact number
     */
    public Customer(int id, String username, String password, String email, String phoneNumber) {
        super(id, username, password, email);
        this.phoneNumber = phoneNumber;
    }

    /**
     * Retrieves the phone number of the customer.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the customer.
     *
     * @param phoneNumber the new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}