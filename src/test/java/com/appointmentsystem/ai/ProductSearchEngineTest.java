package com.appointmentsystem.ai;

import com.appointmentsystem.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ProductSearchEngine class.
 *
 * @author Rahaf
 * @version 1.0
 */
class ProductSearchEngineTest {

    /** The ProductRepository instance used to hold test data. */
    private ProductRepository repository;

    /** The ProductSearchEngine instance used as a test fixture. */
    private ProductSearchEngine searchEngine;

    /**
     * Sets up the test environment before each test method.
     */
    @BeforeEach
    void setUp() {
        repository = new ProductRepository();
        
        repository.addProduct(new Product(1, "Toyota Corolla", "Fast economic car for daily city driving", 15000.0, "Cars", 101));
        repository.addProduct(new Product(2, "Luxury Leather Sofa", "Comfortable premium leather sofa for the living room", 500.0, "Furniture", 102));
        repository.addProduct(new Product(3, "Honda Civic", "Reliable economic sedan car with low fuel consumption", 16000.0, "Cars", 101));
        repository.addProduct(new Product(4, "Office Desk", "Wooden desk suitable for home office work", 120.0, "Furniture", 103));

        searchEngine = new ProductSearchEngine(repository);
    }

    /**
     * Tests searching for products that match a specific category and feature.
     */
    @Test
    void testSearchReturnsCorrectCategoryMatches() {
        List<Product> results = searchEngine.search("economic car", 5);
        
        assertFalse(results.isEmpty());
        for (Product p : results) {
            assertEquals("Cars", p.getCategory());
        }
    }

    /**
     * Tests searching for products using furniture-related keywords.
     */
    @Test
    void testSearchFurnitureMatches() {
        List<Product> results = searchEngine.search("leather sofa living room", 5);
        
        assertFalse(results.isEmpty());
        assertEquals("Luxury Leather Sofa", results.get(0).getName());
    }

    /**
     * Tests searching with a query that has no matching keywords in the repository.
     */
    @Test
    void testSearchWithNoMatchReturnsEmptyList() {
        List<Product> results = searchEngine.search("smartphone laptop electronics", 5);
        
        assertTrue(results.isEmpty());
    }

    /**
     * Tests the topN limit parameter.
     */
    @Test
    void testSearchRespectsTopNLimit() {
        List<Product> results = searchEngine.search("economic car", 1);
        
        assertEquals(1, results.size());
    }

    /**
     * Tests searching with a null or empty query.
     */
    @Test
    void testSearchWithEmptyQuery() {
        List<Product> nullResults = searchEngine.search(null, 5);
        List<Product> emptyResults = searchEngine.search("   ", 5);
        
        assertTrue(nullResults.isEmpty());
        assertTrue(emptyResults.isEmpty());
    }

    /**
     * Tests initializing the search engine with an empty repository.
     */
    @Test
    void testSearchWithEmptyRepository() {
        ProductRepository emptyRepo = new ProductRepository();
        ProductSearchEngine emptyEngine = new ProductSearchEngine(emptyRepo);
        
        List<Product> results = emptyEngine.search("car", 5);
        
        assertTrue(results.isEmpty());
    }

    /**
     * Tests that the search results are ranked by similarity.
     */
    @Test
    void testSearchResultsAreRankedBySimilarity() {
        List<Product> results = searchEngine.search("economic car city", 5);
        
        assertTrue(results.size() >= 2);
        assertEquals("Toyota Corolla", results.get(0).getName());
        assertEquals("Honda Civic", results.get(1).getName());
    }
}