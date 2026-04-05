package com.appointmentsystem.observer;

/**
 * The Subject interface declares methods for managing observers.
 * 
 * @author Shahd
 * @version 1.0
 */
public interface Subject {

    /**
     * Attaches an observer to the subject.
     *
     * @param observer the observer to attach
     */
    void attach(Observer observer);

    /**
     * Detaches an observer from the subject.
     *
     * @param observer the observer to detach
     */
    void detach(Observer observer);

    /**
     * Notifies all observers about an event.
     *
     * @param message the message to send to observers
     */
    void notifyObservers(String message);
}