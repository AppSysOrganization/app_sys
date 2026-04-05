package com.appointmentsystem.repository;

import com.appointmentsystem.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the InMemoryAppointmentRepository class.
 *
 * @author Rahaf
 * @version 1.0
 */
class InMemoryAppointmentRepositoryTest {

    /** The repository instance under test. */
    private InMemoryAppointmentRepository repo;

    /** A Supplier object used for creating test appointments. */
    private Supplier supplier;

    /**
     * Initializes the repository and a sample supplier before each test.
     */
    @BeforeEach
    void setUp() {
        repo = new InMemoryAppointmentRepository();
        supplier = new Supplier(1, "Dr. Sam", "p", "e@m.com", "Gen");
    }

    /**
     * Tests the save and findById functionality.
     */
    @Test
    void testSaveAndFindById() {
        Appointment appt = new Appointment(1, LocalDateTime.now(), LocalDateTime.now().plusHours(1), 
                                          AppointmentType.IN_PERSON, 1, supplier);
        repo.save(appt);

        assertTrue(repo.findById(1).isPresent());
    }

    /**
     * Tests the retrieval of appointments by their status.
     */
    @Test
    void testFindByStatus() {
        Appointment appt1 = new Appointment(1, LocalDateTime.now(), LocalDateTime.now().plusHours(1), 
                                           AppointmentType.IN_PERSON, 1, supplier);
        appt1.setStatus(AppointmentStatus.APPROVED);

        Appointment appt2 = new Appointment(2, LocalDateTime.now(), LocalDateTime.now().plusHours(1), 
                                           AppointmentType.IN_PERSON, 1, supplier);
        appt2.setStatus(AppointmentStatus.PENDING);

        repo.save(appt1);
        repo.save(appt2);

        List<Appointment> approved = repo.findByStatus(AppointmentStatus.APPROVED);
        
        assertEquals(1, approved.size());
        assertEquals(1, approved.get(0).getId());
    }
}
