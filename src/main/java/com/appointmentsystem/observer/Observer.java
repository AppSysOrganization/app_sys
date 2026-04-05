package com.appointmentsystem.observer;

/**
 * The Observer interface declares the update method, used by subjects.
 * 
 * @author Rahaf
 * @version 1.0
 */
public interface Observer {

    /**
     * Receives update from the subject.
     *
     * @param message the notification message
     */
    void update(String message);
}