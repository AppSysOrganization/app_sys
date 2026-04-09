package com.appointmentsystem.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DocumentChunker class.
 * Verifies the functionality of text splitting, cleaning, and combining.
 *
 * @author Elham
 * @version 1.0
 */
class DocumentChunkerTest {

    /** The DocumentChunker instance used as a test fixture. */
    private DocumentChunker chunker;

    /**
     * Sets up the test environment before each test method.
     * Initializes a new DocumentChunker object.
     */
    @BeforeEach
    void setUp() {
        chunker = new DocumentChunker();
    }

    /**
     * Tests chunking a normal text with punctuation, stop words, and numbers.
     * Verifies that punctuation is removed, stop words and pure numbers are excluded.
     */
    @Test
    void testChunkWithNormalText() {
        String text = "The economic car, model 2024 runs in the city!";
        List<String> expected = Arrays.asList("economic", "car", "model", "runs", "city");
        
        List<String> result = chunker.chunk(text);
        
        assertEquals(expected, result);
    }

    /**
     * Tests chunking text that contains only stop words.
     * Verifies that the result is an empty list.
     */
    @Test
    void testChunkWithOnlyStopWords() {
        String text = "the is a an of in on";
        List<String> result = chunker.chunk(text);
        
        assertTrue(result.isEmpty());
    }

    /**
     * Tests chunking text that contains only numbers and punctuation.
     * Verifies that pure numbers are excluded and an empty list is returned.
     */
    @Test
    void testChunkWithOnlyNumbers() {
        String text = "123 456 . 789";
        List<String> result = chunker.chunk(text);
        
        assertTrue(result.isEmpty());
    }

    /**
     * Tests chunking a null input.
     * Verifies that an empty list is returned instead of a NullPointerException.
     */
    @Test
    void testChunkWithNullInput() {
        List<String> result = chunker.chunk(null);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests chunking an empty string input.
     * Verifies that an empty list is returned.
     */
    @Test
    void testChunkWithEmptyInput() {
        List<String> result = chunker.chunk("   ");
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests combining a valid list of chunks into a single string.
     * Verifies that chunks are joined correctly with spaces.
     */
    @Test
    void testCombineChunksWithValidList() {
        List<String> chunks = Arrays.asList("economic", "car", "model");
        String expected = "economic car model";
        
        String result = chunker.combineChunks(chunks);
        
        assertEquals(expected, result);
    }

    /**
     * Tests combining a null list of chunks.
     * Verifies that an empty string is returned.
     */
    @Test
    void testCombineChunksWithNullList() {
        String result = chunker.combineChunks(null);
        
        assertEquals("", result);
    }

    /**
     * Tests combining an empty list of chunks.
     * Verifies that an empty string is returned.
     */
    @Test
    void testCombineChunksWithEmptyList() {
        String result = chunker.combineChunks(Arrays.asList());
        
        assertEquals("", result);
    }
}