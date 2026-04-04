package com.appointmentsystem.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Appointment class.
 * 
 * @author Team
 * @version 1.0
 */
class AppointmentTest {

    /** The Appointment instance used as a test fixture. */
    private Appointment appointment;

    /** The Supplier instance used for testing. */
    private Supplier supplier;

    /** The first Customer instance used for testing. */
    private Customer customer1;

    /** The second Customer instance used for testing. */
    private Customer customer2;

    /**
     * Sets up test data before each test.
     */
    @BeforeEach
    void setUp() {
        supplier = new Supplier(2, "drAhmed", "pass123", "ahmed@clinic.com", "Dentist");
        customer1 = new Customer(10, "c1", "p", "c1@m.com", "0501");
        customer2 = new Customer(11, "c2", "p", "c2@m.com", "0502");
        
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        LocalDateTime end = start.plusHours(1);
        
        appointment = new Appointment(101, start, end, AppointmentType.IN_PERSON, 1, supplier);
    }

    /**
     * Tests the initial creation state of an appointment.
     */
    @Test
    void testAppointmentCreation() {
        assertEquals(101, appointment.getId());
        assertEquals(AppointmentStatus.PENDING, appointment.getStatus());
        assertEquals(AppointmentType.IN_PERSON, appointment.getType());
        assertTrue(appointment.getCustomers().isEmpty());
        assertEquals(0, appointment.getBookedCount());
    }

    /**
     * Tests changing status to APPROVED.
     */
    @Test
    void testStatusChangeApproved() {
        appointment.setStatus(AppointmentStatus.APPROVED);
        assertEquals(AppointmentStatus.APPROVED, appointment.getStatus());
    }
    
    /**
     * Tests changing status to REJECTED.
     */
    @Test
    void testStatusRejected() {
        appointment.setStatus(AppointmentStatus.REJECTED);
        assertEquals(AppointmentStatus.REJECTED, appointment.getStatus());
    }
    
    /**
     * Tests setting notes for an appointment.
     */
    @Test
    void testNotes() {
        String details = "Patient requires wheelchair access.";
        appointment.setNotes(details);
        assertEquals(details, appointment.getNotes());
    }
    
    /**
     * Tests adding a customer to the appointment list.
     */
    @Test
    void testAddCustomer() {
        appointment.addCustomer(customer1);
        assertEquals(1, appointment.getBookedCount());
        assertTrue(appointment.getCustomers().contains(customer1));
    }

    /**
     * Tests removing a customer from the appointment list.
     */
    @Test
    void testRemoveCustomer() {
        appointment.addCustomer(customer1);
        appointment.removeCustomer(customer1);
        assertEquals(0, appointment.getBookedCount());
        assertFalse(appointment.getCustomers().contains(customer1));
    }
    
    /**
     * Tests group booking functionality.
     */
    @Test
    void testGroupBooking() {
        appointment.setMaxParticipants(5);
        appointment.addCustomer(customer1);
        appointment.addCustomer(customer2);
        assertEquals(2, appointment.getBookedCount());
    }
    
    /**
     * Tests setters for ID, Time, Type, and Supplier.
     */
    @Test
    void testBasicSetters() {
        appointment.setId(999);
        assertEquals(999, appointment.getId());

        LocalDateTime newStart = LocalDateTime.now().plusDays(5);
        appointment.setStartTime(newStart);
        assertEquals(newStart, appointment.getStartTime());

        LocalDateTime newEnd = LocalDateTime.now().plusDays(5).plusHours(2);
        appointment.setEndTime(newEnd);
        assertEquals(newEnd, appointment.getEndTime());

        Supplier newSup = new Supplier(5, "Dr. New", "p", "e", "s");
        appointment.setSupplier(newSup);
        assertEquals(newSup, appointment.getSupplier());
    }

    /**
     * Tests that different appointment types are stored correctly.
     */
    @Test
    void testSetAppointmentType() {
        appointment.setType(AppointmentType.URGENT);
        assertEquals(AppointmentType.URGENT, appointment.getType());

        appointment.setType(AppointmentType.GROUP);
        assertEquals(AppointmentType.GROUP, appointment.getType());

        appointment.setType(AppointmentType.VIRTUAL);
        assertEquals(AppointmentType.VIRTUAL, appointment.getType());
    }

    /**
     * Tests that an appointment created with the default constructor has no linked product.
     */
    @Test
    void testDefaultAppointmentHasNoProduct() {
        assertNull(appointment.getProduct());
    }

    /**
     * Tests setting and getting a product for an appointment.
     */
    @Test
    void testSetAndGetProduct() {
        Product product = new Product(1, "Teeth Whitening", "Professional home kit", 49.99, "Dental Care", 2);
        
        appointment.setProduct(product);
        
        assertNotNull(appointment.getProduct());
        assertEquals(product, appointment.getProduct());
        assertEquals("Teeth Whitening", appointment.getProduct().getName());
        assertEquals(49.99, appointment.getProduct().getPrice());
    }

    /**
     * Tests creating an appointment using the overloaded constructor that includes a product.
     */
    @Test
    void testAppointmentCreationWithProduct() {
        Product product = new Product(5, "Braces", "Metal wire braces for teeth alignment", 1000.0, "Orthodontics", 2);
        LocalDateTime start = LocalDateTime.now().plusDays(2).withHour(12).withMinute(0);
        LocalDateTime end = start.plusHours(1);

        Appointment productAppointment = new Appointment(102, start, end, AppointmentType.IN_PERSON, 1, supplier, product);

        assertNotNull(productAppointment.getProduct());
        assertEquals(5, productAppointment.getProduct().getId());
        assertEquals("Braces", productAppointment.getProduct().getName());
    }
}