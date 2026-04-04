package com.appointmentsystem.model;

/**
 * Represents a Product offered by a Supplier in the exhibition system.
 * 
 * @author Shahd.
 * @version 1.1
 */
public class Product {

    /** The unique identifier for the product. */
    private int id;

    /** The name of the product. */
    private String name;

    /** The description of the product. */
    private String description;

    /** The price of the product. */
    private double price;

    /** The category of the product. */
    private String category;

    /** The unique identifier of the supplier who owns this product. */
    private int supplierId;

    /**
     * Constructs a new Product with default values.
     */
    public Product() {
    }

    /**
     * Constructs a new Product with full details.
     *
     * @param id          the unique identifier
     * @param name        the name of the product
     * @param description the description of the product
     * @param price       the price of the product
     * @param category    the category of the product
     * @param supplierId  the id of the supplier who owns this product
     */
    public Product(int id, String name, String description, double price, String category, int supplierId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.supplierId = supplierId;
    }

    /**
     * Retrieves the product ID.
     *
     * @return the product ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the product ID.
     *
     * @param id the new product ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retrieves the product name.
     *
     * @return the product name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the product name.
     *
     * @param name the new product name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the product description.
     *
     * @return the product description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the product description.
     *
     * @param description the new product description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the product price.
     *
     * @return the product price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the product price.
     *
     * @param price the new product price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Retrieves the product category.
     *
     * @return the product category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the product category.
     *
     * @param category the new product category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Retrieves the supplier ID.
     *
     * @return the supplier ID
     */
    public int getSupplierId() {
        return supplierId;
    }

    /**
     * Sets the supplier ID.
     *
     * @param supplierId the new supplier ID
     */
    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }
}