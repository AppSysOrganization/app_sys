package com.appointmentsystem.service;

import com.appointmentsystem.model.User;
import com.appointmentsystem.repository.InMemoryUserRepository;

import java.util.Optional;

/**
 * Service class for handling authentication logic.
 * 
 * @author Shahd
 * @version 1.0
 */
public class AuthService {

    /** The user repository. */
    private InMemoryUserRepository userRepository;

    /** The currently logged-in user. */
    private User currentUser;

    /**
     * Constructs an AuthService with a given repository.
     *
     * @param userRepository the repository to fetch users from
     */
    public AuthService(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
        this.currentUser = null;
    }

    /**
     * Authenticates a user by username and password.
     *
     * @param username the username
     * @param password the password
     * @return the User object if login successful, null otherwise
     */
    public User login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                this.currentUser = user;
                return user;
            }
        }
        return null;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Retrieves the currently logged-in user.
     *
     * @return the current User or null if no one is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
}