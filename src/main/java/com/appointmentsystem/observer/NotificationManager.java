package com.appointmentsystem.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Subject implementation.
 * Manages a list of observers and notifies them of changes.
 * 
 * @author Team
 * @version 1.0
 */
public class NotificationManager implements Subject {

    /** The list of registered observers. */
    private List<Observer> observers;

    /**
     * Constructs a new NotificationManager.
     */
    public NotificationManager() {
        this.observers = new ArrayList<>();
    }

    /**
     * Adds an observer to the notification list.
     *
     * @param observer the observer to add
     */
    @Override
    public void attach(Observer observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Removes an observer from the notification list.
     *
     * @param observer the observer to remove
     */
    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Sends a notification message to all registered observers.
     *
     * @param message the message to broadcast
     */
    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}