package com.appointmentsystem.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Product class.
 *
 * @author Shahd
 * @version 1.0
 */
class ProductTest {

    /** The Product instance used as a test fixture. */
    private Product product;

    /**
     * Sets up the test environment before each test method.
     */
    @BeforeEach
    void setUp() {
        product = new Product(1, "Modern Sofa", "Comfortable 3-seater leather sofa", 499.99, "Furniture", 10);
    }

    /**
     * Tests the retrieval of initial product values set by the constructor.
     */
    @Test
    void testProductConstructorAndGetters() {
        assertEquals(1, product.getId());
        assertEquals("Modern Sofa", product.getName());
        assertEquals("Comfortable 3-seater leather sofa", product.getDescription());
        assertEquals(499.99, product.getPrice());
        assertEquals("Furniture", product.getCategory());
        assertEquals(10, product.getSupplierId());
    }

    /**
     * Tests setting a new name for the product.
     */
    @Test
    void testSetName() {
        product.setName("Luxury Sofa");
        assertEquals("Luxury Sofa", product.getName());
    }

    /**
     * Tests setting a new description for the product.
     */
    @Test
    void testSetDescription() {
        product.setDescription("New premium fabric description");
        assertEquals("New premium fabric description", product.getDescription());
    }

    /**
     * Tests setting a new price for the product.
     */
    @Test
    void testSetPrice() {
        product.setPrice(350.50);
        assertEquals(350.50, product.getPrice());
    }

    /**
     * Tests setting a new category for the product.
     */
    @Test
    void testSetCategory() {
        product.setCategory("Outdoor Furniture");
        assertEquals("Outdoor Furniture", product.getCategory());
    }

    /**
     * Tests setting a new supplier ID for the product.
     */
    @Test
    void testSetSupplierId() {
        product.setSupplierId(20);
        assertEquals(20, product.getSupplierId());
    }

    /**
     * Tests setting a new ID for the product.
     */
    @Test
    void testSetId() {
        product.setId(5);
        assertEquals(5, product.getId());
    }
}