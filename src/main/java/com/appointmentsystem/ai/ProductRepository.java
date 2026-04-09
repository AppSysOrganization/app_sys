package com.appointmentsystem.ai;

import com.appointmentsystem.model.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an in-memory repository for storing and retrieving products.
 * 
 * @author Rahaf
 * @version 1.0
 */
public class ProductRepository {

    /** The in-memory list storing all products added by suppliers. */
    private List<Product> products;

    /**
     * Constructs a new ProductRepository.
     */
    public ProductRepository() {
        this.products = new ArrayList<>();
    }

    /**
     * Adds a new product to the repository.
     *
     * @param product the product to add
     */
    public void addProduct(Product product) {
        products.add(product);
    }

    /**
     * Retrieves all products stored in the repository.
     *
     * @return a list of all products
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    /**
     * Retrieves a list of products belonging to a specific supplier.
     *
     * @param supplierId the unique identifier of the supplier
     * @return a list of products belonging to the specified supplier
     */
    public List<Product> getProductsBySupplierId(int supplierId) {
        return products.stream()
                .filter(p -> p.getSupplierId() == supplierId)
                .collect(Collectors.toList());
    }
    
    /**
     * Removes a product from the repository.
     *
     * @param product the product to remove
     * @return true if removed successfully
     */
    public boolean removeProduct(Product product) {
        return products.remove(product);
    }
}