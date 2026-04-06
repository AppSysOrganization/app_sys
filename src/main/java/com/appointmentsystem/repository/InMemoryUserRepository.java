package com.appointmentsystem.repository;

import com.appointmentsystem.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of the Repository for User entities.
 * 
 * @author Shahd
 * @version 1.0
 */
public class InMemoryUserRepository implements Repository<User> {

    /** The in-memory storage map for users. */
    private Map<Integer, User> storage = new HashMap<>();

    /**
     * Saves a user to the storage.
     *
     * @param user the user to save
     */
    @Override
    public void save(User user) {
        if (user != null) {
            storage.put(user.getId(), user);
        }
    }

    /**
     * Finds a user by ID.
     *
     * @param id the user ID
     * @return an Optional containing the user if found
     */
    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * Retrieves all users.
     *
     * @return list of all users
     */
    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the user ID to delete
     */
    @Override
    public void delete(int id) {
        storage.remove(id);
    }

    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found
     */
    public Optional<User> findByUsername(String username) {
        return storage.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }
    
    /**
     * Loads a list of users into the repository.
     *
     * @param users the list of users to load
     */
    public void loadAll(List<User> users) {
        for (User user : users) {
            if (user != null) {
                storage.put(user.getId(), user);
            }
        }
    } 
}