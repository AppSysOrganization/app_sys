package com.appointmentsystem.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic Repository interface for basic CRUD operations.
 * 
 * @param <T> the type of the entity
 * @author Rahaf
 * @version 1.0
 */
public interface Repository<T> {

    /**
     * Saves a given entity.
     *
     * @param entity the entity to save
     */
    void save(T entity);

    /**
     * Retrieves an entity by its ID.
     *
     * @param id the ID of the entity
     * @return an Optional containing the entity, or empty if not found
     */
    Optional<T> findById(int id);

    /**
     * Retrieves all entities.
     *
     * @return a list of all entities
     */
    List<T> findAll();

    /**
     * Deletes an entity by its ID.
     *
     * @param id the ID of the entity to delete
     */
    void delete(int id);
}