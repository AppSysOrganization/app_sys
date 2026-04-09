package com.appointmentsystem.service;

import com.appointmentsystem.model.*;
import com.appointmentsystem.observer.NotificationManager;
import com.appointmentsystem.repository.InMemoryAppointmentRepository;
import com.appointmentsystem.strategy.BookingRuleStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.startsWith;

/**
 * Unit tests for AppointmentService class.
 * 
 * @author Team
 * @version 1.2
 */
class AppointmentServiceTest {

    /** Mock repository for appointment data. */
    private InMemoryAppointmentRepository mockRepo;
    
    /** Mock notification manager. */
    private NotificationManager mockNotifier;
    
    /** Mock validation rule. */
    private BookingRuleStrategy mockRule;
    
    /** The service instance under test. */
    private AppointmentService service;
    
    /** Dummy supplier for test data. */
    private Supplier supplier;
    
    /** Dummy customer for test data. */
    private Customer customer;
    
    /** Dummy admin for test data. */
    private Admin admin;

    /**
     * Initializes mocks and common test data before each test.
     */
    @BeforeEach
    void setUp() {
        mockRepo = mock(InMemoryAppointmentRepository.class);
        mockNotifier = mock(NotificationManager.class);
        mockRule = mock(BookingRuleStrategy.class);
        
        service = new AppointmentService(mockRepo, mockNotifier, Arrays.asList(mockRule));
        
        supplier = new Supplier(1, "Dr. Sam", "p", "e@m.com", "Gen");
        customer = new Customer(3, "Ali", "p", "a@u.com", "050");
        admin = new Admin(99, "AdminUser", "adminPass", "admin@sys.com");
    }

    @Test
    void testBookAppointmentSuccess() {
        Appointment appt = new Appointment(1, LocalDateTime.now().plusDays(1), 
                LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.APPROVED);
        
        when(mockRepo.findById(1)).thenReturn(Optional.of(appt));
        when(mockRule.isValid(appt)).thenReturn(true);

        String result = service.bookAppointment(1, customer);

        assertNull(result, "Booking should succeed (return null)");
        assertEquals(AppointmentStatus.BOOKED, appt.getStatus(), "Status should be BOOKED");
        assertTrue(appt.getCustomers().contains(customer), "Customer should be added");
        verify(mockRepo, times(1)).save(appt);
        verify(mockNotifier, times(3)).notifyObservers(anyString());
    }
    
    @Test
    void testBookAppointmentFailDueToRuleViolation() {
        Appointment appt = new Appointment(1, LocalDateTime.now().plusDays(1), 
                LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.APPROVED);
        
        when(mockRepo.findById(1)).thenReturn(Optional.of(appt));
        when(mockRule.isValid(appt)).thenReturn(false);

        String result = service.bookAppointment(1, customer);

        assertNotNull(result, "Should return an error message");
        assertEquals(AppointmentStatus.APPROVED, appt.getStatus(), "Status should remain APPROVED");
        assertFalse(appt.getCustomers().contains(customer), "Customer should NOT be added");
        verify(mockNotifier, never()).notifyObservers(anyString());
    }

    @Test
    void testSupplierCancelPending() {
        Appointment appt = new Appointment(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.PENDING);
        
        when(mockRepo.findById(1)).thenReturn(Optional.of(appt));

        String result = service.cancelAppointmentBySupplier(1, supplier);
        
        assertNull(result, "Operation should succeed (return null)");
        assertEquals(AppointmentStatus.CANCELLED, appt.getStatus());
    }

    @Test
    void testSupplierCancelApprovedNoBookings() {
        Appointment appt = new Appointment(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.APPROVED);
        
        when(mockRepo.findById(1)).thenReturn(Optional.of(appt));

        String result = service.cancelAppointmentBySupplier(1, supplier);
        
        assertNull(result, "Operation should succeed");
        assertEquals(AppointmentStatus.CANCELLED, appt.getStatus());
    }

    @Test
    void testSupplierCancelApprovedWithBookingsFail() {
        Appointment appt = new Appointment(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 2, supplier);
        appt.setStatus(AppointmentStatus.APPROVED);
        appt.addCustomer(customer);
        
        when(mockRepo.findById(1)).thenReturn(Optional.of(appt));

        String result = service.cancelAppointmentBySupplier(1, supplier);
        
        assertNotNull(result, "Should return an error message");
        assertEquals("Cannot cancel: This appointment has already been booked by users.", result);
        assertEquals(AppointmentStatus.APPROVED, appt.getStatus(), "Status should remain APPROVED");
    }
    
    @Test
    void testViewAvailableSlots() {
        Appointment availableAppt = new Appointment(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 5, supplier);
        availableAppt.setStatus(AppointmentStatus.APPROVED);
        
        Appointment fullAppt = new Appointment(2, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        fullAppt.setStatus(AppointmentStatus.APPROVED);
        fullAppt.addCustomer(customer);
        
        Appointment pendingAppt = new Appointment(3, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(3).plusHours(1), AppointmentType.IN_PERSON, 5, supplier);
        pendingAppt.setStatus(AppointmentStatus.PENDING);

        List<Appointment> allAppts = new ArrayList<>(Arrays.asList(availableAppt, fullAppt, pendingAppt));
        when(mockRepo.findAll()).thenReturn(allAppts);

        List<Appointment> result = service.getAvailableAppointments();

        assertEquals(1, result.size(), "Only one available appointment should be returned");
        assertEquals(availableAppt.getId(), result.get(0).getId(), "The available appointment should be the one with slots");
        assertFalse(result.contains(fullAppt), "Fully booked slots should not be displayed");
        assertFalse(result.contains(pendingAppt), "Pending slots should not be displayed");
    }

    @Test
    void testCustomerCancelFutureAppointmentSuccess() {
        Appointment appt = new Appointment(10, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.BOOKED);
        appt.addCustomer(customer);
        
        when(mockRepo.findById(10)).thenReturn(Optional.of(appt));

        String result = service.cancelAppointment(10, customer);

        assertNull(result, "Cancellation should succeed");
        assertEquals(AppointmentStatus.APPROVED, appt.getStatus(), "Status should revert to APPROVED");
        assertTrue(appt.getCustomers().isEmpty(), "Customer should be removed from list");
        verify(mockNotifier, times(1)).notifyObservers(anyString());
    }

    @Test
    void testCustomerCancelPastAppointmentFail() {
        Appointment appt = new Appointment(11, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.BOOKED);
        appt.addCustomer(customer);
        
        when(mockRepo.findById(11)).thenReturn(Optional.of(appt));

        String result = service.cancelAppointment(11, customer);

        assertNotNull(result, "Cancellation should fail");
        assertEquals("Cannot cancel past appointments.", result);
        assertEquals(AppointmentStatus.BOOKED, appt.getStatus(), "Status should remain BOOKED");
    }

    @Test
    void testAdminCancelAnyReservation() {
        Appointment appt = new Appointment(12, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.BOOKED);
        appt.addCustomer(customer);
        
        when(mockRepo.findById(12)).thenReturn(Optional.of(appt));

        String result = service.cancelAppointment(12, admin);

        assertNull(result, "Admin cancellation should succeed");
        assertEquals(AppointmentStatus.CANCELLED, appt.getStatus(), "Status should be CANCELLED");
        assertTrue(appt.getCustomers().isEmpty(), "All customers should be removed");
        verify(mockNotifier, times(2)).notifyObservers(anyString());
    }

    @Test
    void testProposeAppointmentSuccess() {
        Appointment appt = new Appointment(20, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        
        boolean result = service.proposeAppointment(appt);

        assertTrue(result, "Proposal should succeed");
        assertEquals(AppointmentStatus.PENDING, appt.getStatus(), "Status must be set to PENDING");
        verify(mockRepo).save(appt);
    }

    @Test
    void testProposeAppointmentNullFail() {
        boolean result = service.proposeAppointment(null);
        assertFalse(result, "Proposal should fail for null");
    }

    @Test
    void testApproveAppointmentSuccess() {
        Appointment appt = new Appointment(21, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.PENDING);
        
        when(mockRepo.findById(21)).thenReturn(Optional.of(appt));

        boolean result = service.approveAppointment(21, admin);

        assertTrue(result, "Approval should succeed");
        assertEquals(AppointmentStatus.APPROVED, appt.getStatus(), "Status should be APPROVED");
        verify(mockNotifier, times(1)).notifyObservers(anyString());
    }

    @Test
    void testRejectAppointmentSuccess() {
        Appointment appt = new Appointment(22, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.PENDING);
        
        when(mockRepo.findById(22)).thenReturn(Optional.of(appt));

        boolean result = service.rejectAppointment(22);

        assertTrue(result, "Rejection should succeed");
        assertEquals(AppointmentStatus.REJECTED, appt.getStatus(), "Status should be REJECTED");
        verify(mockNotifier, times(1)).notifyObservers(anyString());
    }

    @Test
    void testRejectAppointmentFailNotPending() {
        Appointment appt = new Appointment(60, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.APPROVED);

        when(mockRepo.findById(60)).thenReturn(Optional.of(appt));

        boolean result = service.rejectAppointment(60);

        assertFalse(result);
        assertEquals(AppointmentStatus.APPROVED, appt.getStatus());
        verify(mockNotifier, never()).notifyObservers(anyString());
    }

    @Test
    void testApproveAppointmentFailNotPending() {
        Appointment appt = new Appointment(23, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.CANCELLED);
        
        when(mockRepo.findById(23)).thenReturn(Optional.of(appt));

        boolean result = service.approveAppointment(23, admin);

        assertFalse(result, "Approval should fail for non-pending appointments");
        assertEquals(AppointmentStatus.CANCELLED, appt.getStatus(), "Status should not change");
        verify(mockNotifier, never()).notifyObservers(anyString());
    }

    @Test
    void testBookAppointmentFullCapacity() {
        Customer otherCustomer = new Customer(5, "Other", "p", "o@m.com", "051");
        Appointment appt = new Appointment(24, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.APPROVED);
        appt.addCustomer(otherCustomer);
        
        when(mockRepo.findById(24)).thenReturn(Optional.of(appt));

        String result = service.bookAppointment(24, customer);

        assertNotNull(result, "Booking should fail");
        assertEquals("Booking failed: No slots available (Full).", result);
    }

    @Test
    void testBookAppointmentTimeConflict() {
        Appointment existing = new Appointment(25, LocalDateTime.now().plusDays(1).withHour(10), LocalDateTime.now().plusDays(1).withHour(11), AppointmentType.IN_PERSON, 1, supplier);
        existing.setStatus(AppointmentStatus.BOOKED);
        existing.addCustomer(customer);

        Appointment newAppt = new Appointment(26, LocalDateTime.now().plusDays(1).withHour(10).plusMinutes(30), LocalDateTime.now().plusDays(1).withHour(11).plusMinutes(30), AppointmentType.IN_PERSON, 1, supplier);
        newAppt.setStatus(AppointmentStatus.APPROVED);

        when(mockRepo.findById(26)).thenReturn(Optional.of(newAppt));
        when(mockRepo.findAll()).thenReturn(Arrays.asList(existing, newAppt));

        String result = service.bookAppointment(26, customer);

        assertNotNull(result, "Booking should fail due to conflict");
        assertEquals("Booking rejected: You have another appointment at the same time.", result);
    }

    @Test
    void testCancelAppointmentUnauthorizedUser() {
        Customer stranger = new Customer(8, "Stranger", "p", "s@m.com", "055");
        Appointment appt = new Appointment(27, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.BOOKED);
        appt.addCustomer(customer);

        when(mockRepo.findById(27)).thenReturn(Optional.of(appt));

        String result = service.cancelAppointment(27, stranger);

        assertNotNull(result, "Should fail due to unauthorized access");
        assertEquals("You are not authorized to cancel this booking.", result);
    }

    @Test
    void testSupplierCancelUnauthorized() {
        Supplier otherSupplier = new Supplier(9, "Dr. John", "p", "j@m.com", "Gen");
        Appointment appt = new Appointment(28, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.PENDING);

        when(mockRepo.findById(28)).thenReturn(Optional.of(appt));

        String result = service.cancelAppointmentBySupplier(28, otherSupplier);

        assertNotNull(result, "Should fail");
        assertEquals("Unauthorized action.", result);
    }

    @Test
    void testValidateRulesDirect() {
        Appointment appt = new Appointment(29, LocalDateTime.now(), LocalDateTime.now().plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        
        when(mockRule.isValid(appt)).thenReturn(true);
        String resultValid = service.validateRules(appt);
        assertTrue(resultValid.isEmpty(), "Should return empty string for valid rules");

        when(mockRule.isValid(appt)).thenReturn(false);
        String resultInvalid = service.validateRules(appt);
        assertTrue(resultInvalid.contains("Rule Violation"), "Should contain violation message");
    }

    @Test
    void testUpdateCompletedAppointmentsSuccess() {
        Appointment pastAppt = new Appointment(30, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        pastAppt.setStatus(AppointmentStatus.BOOKED);
        pastAppt.addCustomer(customer);

        when(mockRepo.findAll()).thenReturn(Arrays.asList(pastAppt));

        service.updateCompletedAppointments();

        assertEquals(AppointmentStatus.COMPLETED, pastAppt.getStatus(), "Past booked appointment should become COMPLETED");
        verify(mockRepo).save(pastAppt);
    }

    @Test
    void testUpdateExpiredAppointmentsSuccess() {
        Appointment pastAppt = new Appointment(31, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        pastAppt.setStatus(AppointmentStatus.APPROVED);

        when(mockRepo.findAll()).thenReturn(Arrays.asList(pastAppt));

        service.updateCompletedAppointments();

        assertEquals(AppointmentStatus.EXPIRED, pastAppt.getStatus(), "Past approved appointment should become EXPIRED");
        verify(mockRepo).save(pastAppt);
    }

    @Test
    void testModifyAppointmentSuccessPending() {
        Appointment appt = new Appointment(40, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(5).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.PENDING);

        when(mockRepo.findById(40)).thenReturn(Optional.of(appt));

        LocalDateTime newStart = LocalDateTime.now().plusDays(6);
        String result = service.modifyAppointmentBySupplier(40, supplier, newStart, newStart.plusHours(2), AppointmentType.VIRTUAL, 5);

        assertNull(result, "Modification should succeed");
        assertEquals(AppointmentStatus.PENDING, appt.getStatus(), "Status should remain PENDING");
        assertEquals(newStart, appt.getStartTime(), "Start time should be updated");
        verify(mockRepo).save(appt);
    }

    @Test
    void testModifyAppointmentSuccessApprovedToPending() {
        Appointment appt = new Appointment(41, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(5).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.APPROVED);

        when(mockRepo.findById(41)).thenReturn(Optional.of(appt));

        String result = service.modifyAppointmentBySupplier(41, supplier, appt.getStartTime(), appt.getEndTime(), AppointmentType.VIRTUAL, 1);

        assertNull(result, "Modification should succeed");
        assertEquals(AppointmentStatus.PENDING, appt.getStatus(), "Status MUST revert to PENDING");
    }

    @Test
    void testModifyAppointmentSuccessRejectedToPending() {
        Appointment appt = new Appointment(44, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(5).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.REJECTED);

        when(mockRepo.findById(44)).thenReturn(Optional.of(appt));

        String result = service.modifyAppointmentBySupplier(44, supplier, appt.getStartTime(), appt.getEndTime(), AppointmentType.VIRTUAL, 1);

        assertNull(result, "Modification should succeed");
        assertEquals(AppointmentStatus.PENDING, appt.getStatus(), "Status MUST revert to PENDING");
    }

    @Test
    void testModifyAppointmentSuccessExpiredToPending() {
        Appointment appt = new Appointment(45, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(5).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.EXPIRED);

        when(mockRepo.findById(45)).thenReturn(Optional.of(appt));

        String result = service.modifyAppointmentBySupplier(45, supplier, appt.getStartTime(), appt.getEndTime(), AppointmentType.VIRTUAL, 1);

        assertNull(result, "Modification should succeed");
        assertEquals(AppointmentStatus.PENDING, appt.getStatus(), "Status MUST revert to PENDING");
    }

    @Test
    void testModifyAppointmentFailIfBooked() {
        Appointment appt = new Appointment(42, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(5).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.BOOKED);
        appt.addCustomer(customer);

        when(mockRepo.findById(42)).thenReturn(Optional.of(appt));

        String result = service.modifyAppointmentBySupplier(42, supplier, appt.getStartTime(), appt.getEndTime(), AppointmentType.VIRTUAL, 1);

        assertNotNull(result, "Should fail");
        assertEquals("Cannot modify: Appointment is already booked or completed.", result);
        assertEquals(AppointmentStatus.BOOKED, appt.getStatus(), "Status should not change");
    }

    @Test
    void testModifyAppointmentFailIfCompleted() {
        Appointment appt = new Appointment(43, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt.setStatus(AppointmentStatus.COMPLETED);

        when(mockRepo.findById(43)).thenReturn(Optional.of(appt));

        String result = service.modifyAppointmentBySupplier(43, supplier, appt.getStartTime(), appt.getEndTime(), AppointmentType.VIRTUAL, 1);

        assertNotNull(result, "Should fail");
        assertEquals("Cannot modify: Appointment is already booked or completed.", result);
        assertEquals(AppointmentStatus.COMPLETED, appt.getStatus(), "Status should not change");
    }

    @Test
    void testGetPendingAppointments() {
        Appointment appt1 = new Appointment(50, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt1.setStatus(AppointmentStatus.PENDING);

        when(mockRepo.findByStatus(AppointmentStatus.PENDING)).thenReturn(Arrays.asList(appt1));

        List<Appointment> result = service.getPendingAppointments();

        assertEquals(1, result.size());
        assertEquals(50, result.get(0).getId());
    }

    @Test
    void testGetAllAppointments() {
        Appointment appt1 = new Appointment(50, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        Appointment appt2 = new Appointment(51, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);

        when(mockRepo.findAll()).thenReturn(Arrays.asList(appt1, appt2));

        List<Appointment> result = service.getAllAppointments();

        assertEquals(2, result.size());
    }

    @Test
    void testGetBookingsByCustomer() {
        Appointment appt1 = new Appointment(50, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt1.setStatus(AppointmentStatus.BOOKED);
        appt1.addCustomer(customer);
        
        Appointment appt2 = new Appointment(51, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        appt2.setStatus(AppointmentStatus.BOOKED);

        when(mockRepo.findAll()).thenReturn(Arrays.asList(appt1, appt2));

        List<Appointment> result = service.getBookingsByCustomer(customer);

        assertEquals(1, result.size());
        assertEquals(50, result.get(0).getId());
    }

    /**
     * US3.1 - Tests successful reminder generation for upcoming appointments.
     * Acceptance: Reminder message generated.
     */
    @Test
    void testSendRemindersForUpcomingAppointmentsSuccess() {
        Appointment upcomingAppt = new Appointment(99, LocalDateTime.now().plusHours(12), LocalDateTime.now().plusHours(13), AppointmentType.IN_PERSON, 1, supplier);
        upcomingAppt.setStatus(AppointmentStatus.BOOKED);
        upcomingAppt.addCustomer(customer);

        when(mockRepo.findAll()).thenReturn(Arrays.asList(upcomingAppt));

        service.sendRemindersForUpcomingAppointments();

        verify(mockNotifier, times(1)).notifyObservers(startsWith(customer.getEmail() + " Reminder:"));
    }

    /**
     * US3.1 - Tests that no reminder is sent if the appointment is not within 24 hours.
     */
    @Test
    void testSendRemindersForUpcomingAppointmentsNoMatch() {
        Appointment farAppt = new Appointment(99, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(5).plusHours(1), AppointmentType.IN_PERSON, 1, supplier);
        farAppt.setStatus(AppointmentStatus.BOOKED);
        farAppt.addCustomer(customer);

        when(mockRepo.findAll()).thenReturn(Arrays.asList(farAppt));

        service.sendRemindersForUpcomingAppointments();

        verify(mockNotifier, never()).notifyObservers(anyString());
    }
}