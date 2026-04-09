package com.appointmentsystem.ai;

import com.appointmentsystem.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ChatBot class.
 *
 * @author Shahd
 * @version 1.0
 */
class ChatBotTest {

    /** The ChatBot instance used as a test fixture. */
    private ChatBot chatBot;

    /**
     * Sets up the test environment before each test method.
     */
    @BeforeEach
    void setUp() {
        ProductRepository repository = new ProductRepository();
        
        repository.addProduct(new Product(1, "Toyota Corolla", "Fast economic car for daily city driving", 15000.0, "Cars", 101));
        repository.addProduct(new Product(2, "Honda Civic", "Reliable economic sedan car with low fuel consumption", 16000.0, "Cars", 101));
        repository.addProduct(new Product(3, "Luxury Sofa", "Comfortable premium leather sofa for the living room", 500.0, "Furniture", 102));

        ProductSearchEngine searchEngine = new ProductSearchEngine(repository);
        chatBot = new ChatBot(searchEngine);
    }

    /**
     * Tests the chatbot response when the user query matches available products.
     */
    @Test
    void testGetResponseWithValidMatch() {
        ChatResponse response = chatBot.getResponse("I am looking for an economic car");
        
        assertNotNull(response);
        assertTrue(response.getResponseText().contains("I found"));
        assertFalse(response.getRecommendedProducts().isEmpty());
        assertEquals(2, response.getRecommendedProducts().size());
    }

    /**
     * Tests the chatbot response when the user query does not match any products.
     */
    @Test
    void testGetResponseWithNoMatch() {
        ChatResponse response = chatBot.getResponse("I need a cheap smartphone");
        
        assertNotNull(response);
        assertTrue(response.getResponseText().contains("Sorry"));
        assertTrue(response.getRecommendedProducts().isEmpty());
    }

    /**
     * Tests the overloaded getResponse method with a custom topN limit.
     */
    @Test
    void testGetResponseRespectsTopNLimit() {
        ChatResponse response = chatBot.getResponse("economic car", 1);
        
        assertNotNull(response);
        assertEquals(1, response.getRecommendedProducts().size());
        assertEquals("Toyota Corolla", response.getRecommendedProducts().get(0).getName());
    }

    /**
     * Tests the chatbot response with a null or empty query.
     */
    @Test
    void testGetResponseWithNullOrEmptyQuery() {
        ChatResponse nullResponse = chatBot.getResponse(null);
        ChatResponse emptyResponse = chatBot.getResponse("");
        
        assertNotNull(nullResponse);
        assertNotNull(emptyResponse);
        assertTrue(nullResponse.getRecommendedProducts().isEmpty());
        assertTrue(emptyResponse.getRecommendedProducts().isEmpty());
    }

    /**
     * Tests that the recommended products returned by the chatbot contain full details.
     */
    @Test
    void testResponseProductsContainFullDetails() {
        ChatResponse response = chatBot.getResponse("leather sofa");
        
        assertFalse(response.getRecommendedProducts().isEmpty());
        Product product = response.getRecommendedProducts().get(0);
        
        assertEquals("Luxury Sofa", product.getName());
        assertEquals(500.0, product.getPrice());
        assertEquals("Comfortable premium leather sofa for the living room", product.getDescription());
        assertEquals(102, product.getSupplierId());
    }
}