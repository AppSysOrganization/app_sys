package com.appointmentsystem.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Supplier who provides appointment slots.
 * 
 * @author Rahaf
 * @version 1.2
 */
public class Supplier extends User {

    /** The field of specialization of the supplier. */
    private String specialization;

    /** The list of products offered by this supplier. */
    private transient List<Product> products;

    /**
     * Constructs a new Supplier with default values.
     */
    public Supplier() {
        this.products = new ArrayList<>();
    }

    /**
     * Constructs a new Supplier.
     *
     * @param id             the unique identifier
     * @param username       the supplier username
     * @param password       the supplier password
     * @param email          the supplier email
     * @param specialization the field of specialization
     */
    public Supplier(int id, String username, String password, String email, String specialization) {
        super(id, username, password, email);
        this.specialization = specialization;
        this.products = new ArrayList<>();
    }

    /**
     * Retrieves the specialization of the supplier.
     *
     * @return the specialization
     */
    public String getSpecialization() {
        return specialization;
    }

    /**
     * Sets the specialization of the supplier.
     *
     * @param specialization the new specialization
     */
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    /**
     * Retrieves the list of products offered by this supplier.
     *
     * @return the list of products
     */
    public List<Product> getProducts() {
        if (products == null) {
            products = new ArrayList<>();
        }
        return products;
    }

    /**
     * Adds a product to the supplier's product list.
     *
     * @param product the product to add
     */
    public void addProduct(Product product) {
        if (products == null) {
            products = new ArrayList<>();
        }
        products.add(product);
    }

    /**
     * Removes a product from the supplier's product list.
     *
     * @param product the product to remove
     * @return true if the product was removed, false otherwise
     */
    public boolean removeProduct(Product product) {
        if (products == null) return false;
        return products.remove(product);
    }

    /**
     * Updates an existing product with new details.
     *
     * @param productId      the id of the product to update
     * @param newName        the new name of the product
     * @param newDescription the new description of the product
     * @param newPrice       the new price of the product
     * @param newCategory    the new category of the product
     * @return true if the product was found and updated, false otherwise
     */
    public boolean updateProduct(int productId, String newName, String newDescription, double newPrice, String newCategory) {
        if (products == null) return false;
        
        for (Product product : products) {
            if (product.getId() == productId) {
                product.setName(newName);
                product.setDescription(newDescription);
                product.setPrice(newPrice);
                product.setCategory(newCategory);
                return true;
            }
        }
        return false;
    }
}