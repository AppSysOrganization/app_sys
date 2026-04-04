package com.appointmentsystem.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Supplier class.
 *
 * @author Rahaf.
 * @version 1.0
 */
class SupplierTest {

    /** The Supplier instance used as a test fixture. */
    private Supplier supplier;

    /** A Product instance used for testing product operations. */
    private Product product;

    /**
     * Sets up the test environment before each test method.
     */
    @BeforeEach
    void setUp() {
        supplier = new Supplier(2, "drAhmed", "pass123", "ahmed@clinic.com", "Dentist");
        product = new Product(1, "Teeth Whitening Kit", "Professional home teeth whitening kit with LED light", 49.99, "Dental Care", 2);
    }

    /**
     * Tests the retrieval of the supplier's specialization.
     */
    @Test
    void testSupplierSpecialization() {
        assertEquals("Dentist", supplier.getSpecialization());
    }

    /**
     * Tests the setting of a new specialization for the supplier.
     */
    @Test
    void testSetSpecialization() {
        supplier.setSpecialization("Cardiologist");
        assertEquals("Cardiologist", supplier.getSpecialization());
    }

    /**
     * Tests adding a product to the supplier's product list.
     */
    @Test
    void testAddProduct() {
        supplier.addProduct(product);
        assertEquals(1, supplier.getProducts().size());
        assertEquals("Teeth Whitening Kit", supplier.getProducts().get(0).getName());
    }

    /**
     * Tests retrieving the product list when no products have been added.
     */
    @Test
    void testGetProductsEmpty() {
        assertNotNull(supplier.getProducts());
        assertEquals(0, supplier.getProducts().size());
    }

    /**
     * Tests removing a product from the supplier's product list.
     */
    @Test
    void testRemoveProduct() {
        supplier.addProduct(product);
        boolean removed = supplier.removeProduct(product);
        assertTrue(removed);
        assertEquals(0, supplier.getProducts().size());
    }

    /**
     * Tests removing a product that does not exist in the supplier's product list.
     */
    @Test
    void testRemoveProductNotFound() {
        Product fakeProduct = new Product(99, "Fake Product", "Not real", 0.0, "Fake", 2);
        boolean removed = supplier.removeProduct(fakeProduct);
        assertFalse(removed);
        assertEquals(0, supplier.getProducts().size());
    }

    /**
     * Tests updating an existing product's details.
     */
    @Test
    void testUpdateProduct() {
        supplier.addProduct(product);
        boolean updated = supplier.updateProduct(1, "Advanced Whitening Kit", "New formula with faster results", 59.99, "Premium Dental");
        assertTrue(updated);
        assertEquals("Advanced Whitening Kit", product.getName());
        assertEquals("New formula with faster results", product.getDescription());
        assertEquals(59.99, product.getPrice());
        assertEquals("Premium Dental", product.getCategory());
    }

    /**
     * Tests updating a product that does not exist in the supplier's product list.
     */
    @Test
    void testUpdateProductNotFound() {
        supplier.addProduct(product);
        boolean updated = supplier.updateProduct(99, "Fake", "Fake", 0.0, "Fake");
        assertFalse(updated);
        assertEquals("Teeth Whitening Kit", product.getName());
    }
}