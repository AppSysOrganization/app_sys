package com.appointmentsystem.service;

import com.appointmentsystem.model.*;
import com.appointmentsystem.observer.NotificationManager;
import com.appointmentsystem.repository.InMemoryAppointmentRepository;
import com.appointmentsystem.strategy.BookingRuleStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing appointments.
 * 
 * @author Team
 * @version 1.4
 */
public class AppointmentService {

    /** The appointment repository. */
    private InMemoryAppointmentRepository appointmentRepo;

    /** The notification manager. */
    private NotificationManager notificationManager;

    /** The list of booking validation rules. */
    private List<BookingRuleStrategy> validationRules;

    /**
     * Constructs an AppointmentService with the required dependencies.
     *
     * @param appointmentRepo     the appointment repository
     * @param notificationManager the notification manager
     * @param validationRules     the list of validation rules
     */
    public AppointmentService(InMemoryAppointmentRepository appointmentRepo,
                              NotificationManager notificationManager,
                              List<BookingRuleStrategy> validationRules) {
        this.appointmentRepo = appointmentRepo;
        this.notificationManager = notificationManager;
        this.validationRules = validationRules;
    }

    /**
     * Validates an appointment against all registered rules.
     *
     * @param appointment the appointment to validate
     * @return empty string if valid, violation message if invalid
     */
    public String validateRules(Appointment appointment) {
        for (BookingRuleStrategy rule : validationRules) {
            if (!rule.isValid(appointment)) {
                return "Rule Violation: " + rule.getClass().getSimpleName();
            }
        }
        return "";
    }

    /**
     * Proposes a new appointment and sets its status to PENDING.
     *
     * @param appointment the appointment to propose
     * @return true if proposed successfully, false otherwise
     */
    public boolean proposeAppointment(Appointment appointment) {
        if (appointment != null) {
            appointment.setStatus(AppointmentStatus.PENDING);
            appointmentRepo.save(appointment);
            return true;
        }
        return false;
    }

    /**
     * Approves a pending appointment.
     *
     * @param appointmentId the ID of the appointment
     * @param admin         the admin approving the appointment
     * @return true if approved successfully, false otherwise
     */
    public boolean approveAppointment(int appointmentId, Admin admin) {
        Appointment appt = appointmentRepo.findById(appointmentId).orElse(null);
        if (appt != null && appt.getStatus() == AppointmentStatus.PENDING) {
            appt.setStatus(AppointmentStatus.APPROVED);
            appointmentRepo.save(appt);
            notificationManager.notifyObservers(appt.getSupplier().getEmail() +
                    " Your appointment ID: " + appointmentId + " has been APPROVED.");
            return true;
        }
        return false;
    }

    /**
     * Rejects a pending appointment.
     *
     * @param appointmentId the ID of the appointment
     * @return true if rejected successfully, false otherwise
     */
    public boolean rejectAppointment(int appointmentId) {
        Appointment appt = appointmentRepo.findById(appointmentId).orElse(null);
        if (appt != null && appt.getStatus() == AppointmentStatus.PENDING) {
            appt.setStatus(AppointmentStatus.REJECTED);
            appointmentRepo.save(appt);
            notificationManager.notifyObservers(appt.getSupplier().getEmail() +
                    " Your appointment ID: " + appointmentId + " has been REJECTED.");
            return true;
        }
        return false;
    }

    /**
     * Cancels an appointment by the supplier.
     *
     * @param appointmentId the ID of the appointment
     * @param supplier      the supplier canceling the appointment
     * @return null if success, error message if failed
     */
    public String cancelAppointmentBySupplier(int appointmentId, Supplier supplier) {
        Appointment appt = appointmentRepo.findById(appointmentId).orElse(null);

        if (appt == null) return "Appointment not found.";
        if (appt.getSupplier().getId() != supplier.getId()) return "Unauthorized action.";

        if (appt.getStatus() == AppointmentStatus.PENDING) {
            appt.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepo.save(appt);
            return null;
        }

        if (appt.getStatus() == AppointmentStatus.APPROVED) {
            if (appt.getBookedCount() > 0) {
                return "Cannot cancel: This appointment has already been booked by users.";
            }
            appt.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepo.save(appt);
            return null;
        }

        return "Cannot cancel appointment in its current state.";
    }

    /**
     * Books an appointment for a customer.
     * Sends an immediate reminder if the appointment starts within 24 hours.
     *
     * @param appointmentId the ID of the appointment
     * @param customer      the customer booking the appointment
     * @return null if success, error message if failed
     */
    public String bookAppointment(int appointmentId, Customer customer) {
        Appointment appt = appointmentRepo.findById(appointmentId).orElse(null);

        if (appt == null) return "Appointment not found.";
        if (appt.getStatus() != AppointmentStatus.APPROVED && appt.getStatus() != AppointmentStatus.BOOKED) {
            return "Appointment is not available.";
        }

        if (appt.getBookedCount() >= appt.getMaxParticipants()) {
            return "Booking failed: No slots available (Full).";
        }

        List<Appointment> existingBookings = getBookingsByCustomer(customer);
        for (Appointment existing : existingBookings) {
            if (appt.getId() == existing.getId()) continue;
            if (appt.getStartTime().isBefore(existing.getEndTime()) &&
                appt.getEndTime().isAfter(existing.getStartTime())) {
                return "Booking rejected: You have another appointment at the same time.";
            }
        }

        for (BookingRuleStrategy rule : validationRules) {
            if (!rule.isValid(appt)) {
                return "Booking failed: Business rule violated.";
            }
        }

        appt.addCustomer(customer);
        appt.setStatus(AppointmentStatus.BOOKED);
        appointmentRepo.save(appt);

        notificationManager.notifyObservers(customer.getEmail() +
                " Booking confirmed for appointment ID: " + appt.getId() + " at " + appt.getStartTime().toLocalDate() + ".");
        notificationManager.notifyObservers(appt.getSupplier().getEmail() +
                " New booking received for appointment ID: " + appt.getId() + " by customer: " + customer.getUsername() + ".");

        if (appt.getStartTime().isBefore(LocalDateTime.now().plusHours(24))) {
            notificationManager.notifyObservers(customer.getEmail() +
                    " Reminder: You have an appointment today at " + appt.getStartTime().toLocalTime() +
                    " with " + appt.getSupplier().getUsername() + ".");
        }

        return null;
    }

    /**
     * Cancels a booked appointment by a user.
     *
     * @param appointmentId the ID of the appointment
     * @param user          the user requesting cancellation
     * @return null if success, error message if failed
     */
    public String cancelAppointment(int appointmentId, User user) {
        Appointment appt = appointmentRepo.findById(appointmentId).orElse(null);

        if (appt == null) return "Appointment not found.";
        if (appt.getStartTime().isBefore(LocalDateTime.now())) {
            return "Cannot cancel past appointments.";
        }

        boolean isAdmin = user instanceof Admin;
        boolean isOwner = appt.getCustomers().stream().anyMatch(c -> c.getId() == user.getId());

        if (!isAdmin && !isOwner) {
            return "You are not authorized to cancel this booking.";
        }

        if (isAdmin) {
            List<Customer> bookedCustomers = new ArrayList<>(appt.getCustomers());
            for (Customer c : bookedCustomers) {
                notificationManager.notifyObservers(c.getEmail() +
                        " Appointment ID: " + appointmentId + " has been cancelled by Admin.");
            }
            notificationManager.notifyObservers(appt.getSupplier().getEmail() +
                    " Admin has cancelled your approved appointment ID: " + appointmentId + ".");
            appt.setStatus(AppointmentStatus.CANCELLED);
            appt.getCustomers().clear();
        } else {
            notificationManager.notifyObservers(appt.getSupplier().getEmail() +
                    " Customer " + user.getUsername() + " cancelled booking for appointment ID: " + appointmentId + ".");
            appt.getCustomers().removeIf(c -> c.getId() == user.getId());
            if (appt.getCustomers().isEmpty()) {
                appt.setStatus(AppointmentStatus.APPROVED);
            }
        }

        appointmentRepo.save(appt);
        return null;
    }

    /**
     * Updates the status of past appointments.
     * BOOKED becomes COMPLETED. APPROVED becomes EXPIRED.
     */
    public void updateCompletedAppointments() {
        LocalDateTime now = LocalDateTime.now();
        List<Appointment> allAppts = appointmentRepo.findAll();

        for (Appointment appt : allAppts) {
            if (appt.getEndTime().isBefore(now)) {
                if (appt.getStatus() == AppointmentStatus.BOOKED) {
                    appt.setStatus(AppointmentStatus.COMPLETED);
                    appointmentRepo.save(appt);
                } else if (appt.getStatus() == AppointmentStatus.APPROVED) {
                    appt.setStatus(AppointmentStatus.EXPIRED);
                    appointmentRepo.save(appt);
                }
            }
        }
    }

    /**
     * Modifies an existing appointment by the supplier.
     * Reverts to PENDING if currently APPROVED, REJECTED, EXPIRED, or CANCELLED.
     *
     * @param appointmentId the ID of the appointment
     * @param supplier      the supplier modifying the appointment
     * @param newStart      the new start time
     * @param newEnd        the new end time
     * @param newType       the new appointment type
     * @param newCapacity   the new maximum capacity
     * @return null if success, error message if failed
     */
    public String modifyAppointmentBySupplier(int appointmentId, Supplier supplier,
                                              LocalDateTime newStart, LocalDateTime newEnd,
                                              AppointmentType newType, int newCapacity) {
        Appointment appt = appointmentRepo.findById(appointmentId).orElse(null);

        if (appt == null) return "Appointment not found.";
        if (appt.getSupplier().getId() != supplier.getId()) return "Unauthorized action.";

        if (appt.getStatus() == AppointmentStatus.BOOKED || appt.getStatus() == AppointmentStatus.COMPLETED) {
            return "Cannot modify: Appointment is already booked or completed.";
        }

        appt.setStartTime(newStart);
        appt.setEndTime(newEnd);
        appt.setType(newType);
        appt.setMaxParticipants(newCapacity);

        if (appt.getStatus() == AppointmentStatus.APPROVED ||
            appt.getStatus() == AppointmentStatus.REJECTED ||
            appt.getStatus() == AppointmentStatus.EXPIRED ||
            appt.getStatus() == AppointmentStatus.CANCELLED) {
            appt.setStatus(AppointmentStatus.PENDING);
        }

        appointmentRepo.save(appt);
        return null;
    }

    /**
     * Checks for upcoming appointments and sends reminder notifications.
     * US3.1 Acceptance: Reminder message generated.
     */
    public void sendRemindersForUpcomingAppointments() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderThreshold = now.plusHours(24);

        List<Appointment> allAppts = appointmentRepo.findAll();

        for (Appointment appt : allAppts) {
            if (appt.getStatus() == AppointmentStatus.BOOKED || appt.getStatus() == AppointmentStatus.APPROVED) {
                if (appt.getStartTime().isAfter(now) && appt.getStartTime().isBefore(reminderThreshold)) {
                    for (Customer c : appt.getCustomers()) {
                        String reminderMessage = c.getEmail() + " Reminder: You have an appointment tomorrow at "
                                + appt.getStartTime().toLocalTime() + " with " + appt.getSupplier().getUsername() + ".";
                        notificationManager.notifyObservers(reminderMessage);
                    }
                }
            }
        }
    }

    /**
     * Retrieves a list of appointments that have available slots.
     *
     * @return the list of available appointments
     */
    public List<Appointment> getAvailableAppointments() {
        return appointmentRepo.findAll().stream()
                .filter(a -> (a.getStatus() == AppointmentStatus.APPROVED || a.getStatus() == AppointmentStatus.BOOKED))
                .filter(a -> a.getBookedCount() < a.getMaxParticipants())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all pending appointments.
     *
     * @return the list of pending appointments
     */
    public List<Appointment> getPendingAppointments() {
        return appointmentRepo.findByStatus(AppointmentStatus.PENDING);
    }

    /**
     * Retrieves all appointments.
     *
     * @return the list of all appointments
     */
    public List<Appointment> getAllAppointments() {
        return appointmentRepo.findAll();
    }

    /**
     * Retrieves all appointments booked by a specific customer.
     *
     * @param customer the customer to filter by
     * @return the list of booked appointments
     */
    public List<Appointment> getBookingsByCustomer(Customer customer) {
        return appointmentRepo.findAll().stream()
                .filter(a -> a.getCustomers().stream().anyMatch(c -> c.getId() == customer.getId()))
                .collect(Collectors.toList());
    }
}