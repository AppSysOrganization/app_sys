package com.appointmentsystem.repository;

import com.appointmentsystem.model.Admin;
import com.appointmentsystem.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the InMemoryUserRepository class.
 *
 * @author Shahd
 * @version 1.0
 */
class InMemoryUserRepositoryTest {

    /** The repository instance used for testing. */
    private InMemoryUserRepository repo;

    /**
     * Initializes the repository before each test method.
     */
    @BeforeEach
    void setUp() {
        repo = new InMemoryUserRepository();
    }

    /**
     * Tests the save and findById functionality.
     */
    @Test
    void testSaveAndFindById() {
        User admin = new Admin(1, "admin", "pass", "admin@test.com");
        repo.save(admin);

        Optional<User> found = repo.findById(1);
        assertTrue(found.isPresent());
        assertEquals("admin", found.get().getUsername());
    }

    /**
     * Tests the retrieval of a user by username.
     */
    @Test
    void testFindByUsername() {
        User admin = new Admin(1, "admin", "pass", "admin@test.com");
        repo.save(admin);

        Optional<User> found = repo.findByUsername("admin");
        assertTrue(found.isPresent());
        
        Optional<User> notFound = repo.findByUsername("unknown");
        assertFalse(notFound.isPresent());
    }

    /**
     * Tests the delete functionality of the repository.
     */
    @Test
    void testDelete() {
        User admin = new Admin(1, "admin", "pass", "admin@test.com");
        repo.save(admin);
        repo.delete(1);
        
        assertFalse(repo.findById(1).isPresent());
    }
}