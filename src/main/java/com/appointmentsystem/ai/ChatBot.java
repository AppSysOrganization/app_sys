package com.appointmentsystem.ai;

import com.appointmentsystem.model.Product;
import java.util.List;

/**
 * Represents the main Chatbot interface for the user.
 * 
 * @author Shahd
 * @version 1.0
 */
public class ChatBot {

    /** The default maximum number of product recommendations to return. */
    private static final int DEFAULT_TOP_N = 3;

    /** The search engine used to retrieve matching products based on the query. */
    private final ProductSearchEngine searchEngine;

    /**
     * Constructs a new ChatBot.
     *
     * @param searchEngine the initialized search engine containing the product index
     */
    public ChatBot(ProductSearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }

    /**
     * Processes the user's message, searches for relevant products, and formulates a response.
     *
     * @param userMessage the natural language query written by the user
     * @return a ChatResponse containing the bot's text and the recommended products
     */
    public ChatResponse getResponse(String userMessage) {
        List<Product> recommendations = searchEngine.search(userMessage, DEFAULT_TOP_N);
        
        String responseText;
        
        if (recommendations.isEmpty()) {
            responseText = "Sorry, I couldn't find any products matching your description. "
                         + "Please try using different keywords.";
        } else {
            responseText = "I found " + recommendations.size() + " product(s) matching your request. "
                         + "Please review the details below. You can book an appointment with the supplier for any of these products.";
        }
        
        return new ChatResponse(responseText, recommendations);
    }

    /**
     * Processes the user's message with a custom limit for the number of recommendations.
     *
     * @param userMessage the natural language query written by the user
     * @param topN the maximum number of products to recommend
     * @return a ChatResponse containing the bot's text and the recommended products
     */
    public ChatResponse getResponse(String userMessage, int topN) {
        List<Product> recommendations = searchEngine.search(userMessage, topN);
        
        String responseText;
        
        if (recommendations.isEmpty()) {
            responseText = "Sorry, I couldn't find any products matching your description. "
                         + "Please try using different keywords.";
        } else {
            responseText = "I found " + recommendations.size() + " product(s) matching your request. "
                         + "Please review the details below. You can book an appointment with the supplier for any of these products.";
        }
        
        return new ChatResponse(responseText, recommendations);
    }
}