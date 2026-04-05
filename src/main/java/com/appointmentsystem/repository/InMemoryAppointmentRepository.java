package com.appointmentsystem.repository;

import com.appointmentsystem.model.Appointment;
import com.appointmentsystem.model.AppointmentStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the Repository for Appointment entities.
 * 
 * @author Elham
 * @version 1.0
 */
public class InMemoryAppointmentRepository implements Repository<Appointment> {

    /** The in-memory storage map for appointments. */
    private Map<Integer, Appointment> storage = new HashMap<>();

    /**
     * Saves an appointment to the storage.
     *
     * @param appointment the appointment to save
     */
    @Override
    public void save(Appointment appointment) {
        if (appointment != null) {
            storage.put(appointment.getId(), appointment);
        }
    }

    /**
     * Finds an appointment by ID.
     *
     * @param id the appointment ID
     * @return an Optional containing the appointment if found
     */
    @Override
    public Optional<Appointment> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * Retrieves all appointments.
     *
     * @return list of all appointments
     */
    @Override
    public List<Appointment> findAll() {
        return new ArrayList<>(storage.values());
    }

    /**
     * Deletes an appointment by ID.
     *
     * @param id the appointment ID to delete
     */
    @Override
    public void delete(int id) {
        storage.remove(id);
    }

    /**
     * Finds all appointments with a specific status.
     *
     * @param status the status to filter by
     * @return list of matching appointments
     */
    public List<Appointment> findByStatus(AppointmentStatus status) {
        return storage.values().stream()
                .filter(a -> a.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Loads a list of appointments into the repository.
     *
     * @param appointments the list of appointments to load
     */
    public void loadAll(List<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            if (appointment != null) {
                storage.put(appointment.getId(), appointment);
            }
        }
    }
}