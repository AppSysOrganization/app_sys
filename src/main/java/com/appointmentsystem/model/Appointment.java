package com.appointmentsystem.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Appointment in the system.
 * Supports individual and group bookings.
 * 
 * @author Team
 * @version 1.0
 */
public class Appointment {

    /** The unique identifier for the appointment. */
    private int id;

    /** The start time of the appointment. */
    private LocalDateTime startTime;

    /** The end time of the appointment. */
    private LocalDateTime endTime;

    /** The current status of the appointment. */
    private AppointmentStatus status;

    /** The type of the appointment. */
    private AppointmentType type;

    /** The maximum number of participants allowed. */
    private int maxParticipants;

    /** The supplier who owns this appointment. */
    private Supplier supplier;

    /** The list of customers booked for this appointment. */
    private List<Customer> customers;

    /** Additional notes for the appointment. */
    private String notes;

    /** The product associated with this appointment. */
    private Product product;

    /**
     * Constructs a new Appointment with default values.
     */
    public Appointment() {
        this.customers = new ArrayList<>();
        this.notes = "";
    }

    /**
     * Constructs a new Appointment.
     *
     * @param id              the unique identifier
     * @param startTime       the start time
     * @param endTime         the end time
     * @param type            the type
     * @param maxParticipants the max capacity
     * @param supplier        the supplier
     */
    public Appointment(int id, LocalDateTime startTime, LocalDateTime endTime, 
                       AppointmentType type, int maxParticipants, Supplier supplier) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.maxParticipants = maxParticipants;
        this.supplier = supplier;
        this.status = AppointmentStatus.PENDING;
        this.customers = new ArrayList<>();
        this.notes = "";
    }

    /**
     * Constructs a new Appointment linked to a specific product.
     *
     * @param id              the unique identifier
     * @param startTime       the start time
     * @param endTime         the end time
     * @param type            the type
     * @param maxParticipants the max capacity
     * @param supplier        the supplier
     * @param product         the product associated with this appointment
     */
    public Appointment(int id, LocalDateTime startTime, LocalDateTime endTime, 
                       AppointmentType type, int maxParticipants, Supplier supplier, Product product) {
        this(id, startTime, endTime, type, maxParticipants, supplier);
        this.product = product;
    }

    /**
     * Retrieves the appointment ID.
     *
     * @return the appointment ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the appointment ID.
     *
     * @param id the new appointment ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retrieves the start time.
     *
     * @return the start time
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time.
     *
     * @param startTime the new start time
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Retrieves the end time.
     *
     * @return the end time
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time.
     *
     * @param endTime the new end time
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Retrieves the appointment status.
     *
     * @return the appointment status
     */
    public AppointmentStatus getStatus() {
        return status;
    }

    /**
     * Sets the appointment status.
     *
     * @param status the new appointment status
     */
    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    /**
     * Retrieves the appointment type.
     *
     * @return the appointment type
     */
    public AppointmentType getType() {
        return type;
    }

    /**
     * Sets the appointment type.
     *
     * @param type the new appointment type
     */
    public void setType(AppointmentType type) {
        this.type = type;
    }

    /**
     * Retrieves the maximum number of participants.
     *
     * @return the max participants
     */
    public int getMaxParticipants() {
        return maxParticipants;
    }

    /**
     * Sets the maximum number of participants.
     *
     * @param maxParticipants the new max participants
     */
    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    /**
     * Retrieves the supplier.
     *
     * @return the supplier
     */
    public Supplier getSupplier() {
        return supplier;
    }

    /**
     * Sets the supplier.
     *
     * @param supplier the new supplier
     */
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    /**
     * Retrieves the notes.
     *
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the notes.
     *
     * @param notes the new notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Retrieves the associated product.
     *
     * @return the associated product, or null if not linked
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the associated product.
     *
     * @param product the product to link
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Retrieves the list of booked customers.
     *
     * @return the list of customers
     */
    public List<Customer> getCustomers() {
        return customers;
    }

    /**
     * Adds a customer to the appointment.
     *
     * @param customer the customer to add
     */
    public void addCustomer(Customer customer) {
        if (!customers.contains(customer)) {
            customers.add(customer);
        }
    }

    /**
     * Removes a customer from the appointment.
     *
     * @param customer the customer to remove
     */
    public void removeCustomer(Customer customer) {
        customers.remove(customer);
    }

    /**
     * Retrieves the current number of booked participants.
     *
     * @return the booked count
     */
    public int getBookedCount() {
        return customers.size();
    }
}