package com.appointmentsystem.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Customer class.
 * 
 * @author Rahaf
 * @version 1.0
 */
class CustomerTest {

    /** The Customer instance used for testing. */
    private Customer customer;

    /**
     * Sets up a new customer before each test.
     */
    @BeforeEach
    void setUp() {
        customer = new Customer(3, "userAli", "userPass", "ali@gmail.com", "0501234567");
    }

    /**
     * Tests the initial phone number set by constructor.
     */
    @Test
    void testCustomerPhoneNumber() {
        assertEquals("0501234567", customer.getPhoneNumber());
    }
    
    /**
     * Tests updating the phone number.
     */
    @Test
    void testSetPhoneNumber() {
        String newNumber = "0559876543";
        customer.setPhoneNumber(newNumber);
        assertEquals(newNumber, customer.getPhoneNumber(), "Phone number should be updated correctly");
    }
}