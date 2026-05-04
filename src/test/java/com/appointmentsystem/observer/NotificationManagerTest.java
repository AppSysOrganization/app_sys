package com.appointmentsystem.observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NotificationManager using Mockito.
 *
 * @author Team
 * @version 1.0
 */
class NotificationManagerTest {

    /** The NotificationManager instance used for testing. */
    private NotificationManager manager;

    /** The first mocked Observer. */
    private Observer mockObserver1;

    /** The second mocked Observer. */
    private Observer mockObserver2;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        manager = new NotificationManager();
        mockObserver1 = mock(Observer.class);
        mockObserver2 = mock(Observer.class);
    }

    /**
     * Tests that notifyObservers successfully sends the message to all attached observers.
     */
    @Test
    void testNotifyObservers() {
        manager.attach(mockObserver1);
        manager.attach(mockObserver2);
        String testMessage = "Appointment Confirmed";
        manager.notifyObservers(testMessage);
        verify(mockObserver1, times(1)).update(testMessage);
        verify(mockObserver2, times(1)).update(testMessage);
    }

    /**
     * Tests that detached observers do not receive notifications.
     */
    @Test
    void testDetachObserver() {
        manager.attach(mockObserver1);
        manager.detach(mockObserver1);
        manager.notifyObservers("Test");
        verify(mockObserver1, never()).update(anyString());
    }

    /**
     * Tests that attaching a null observer does not throw and is ignored.
     */
    @Test
    void testAttachNullObserver() {
        manager.attach(null);
        manager.notifyObservers("Test");
        verify(mockObserver1, never()).update(anyString());
    }

    /**
     * Tests that attaching the same observer twice adds it only once.
     */
    @Test
    void testAttachDuplicateObserver() {
        manager.attach(mockObserver1);
        manager.attach(mockObserver1);
        manager.notifyObservers("Test");
        verify(mockObserver1, times(1)).update("Test");
    }

    /**
     * Tests that notifyObservers with no observers does not throw.
     */
    @Test
    void testNotifyWithNoObservers() {
        assertDoesNotThrow(() -> manager.notifyObservers("Test"));
    }

    /**
     * Tests that detaching an observer that was never attached does not throw.
     */
    @Test
    void testDetachNonAttachedObserver() {
        assertDoesNotThrow(() -> manager.detach(mockObserver1));
    }
}