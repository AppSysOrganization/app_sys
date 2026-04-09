package com.appointmentsystem.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Responsible for splitting text documents into smaller pieces (chunks).
 * 
 * @author Elham
 * @version 1.0
 */
public class DocumentChunker {

    /** A list of common English stop words to be excluded from chunks. */
    private static final List<String> STOP_WORDS = Arrays.asList(
            "the", "a", "an", "is", "are", "was", "were", "in", "on", "at",
            "to", "for", "with", "and", "or", "of", "it", "this", "that",
            "be", "by", "from", "as", "has", "have", "had", "not", "but",
            "if", "so", "than", "too", "very", "can", "will", "just", "should",
            "now", "also", "its", "our", "we", "you", "they", "he", "she"
    );

    /**
     * Splits a given text into small meaningful chunks (words) after cleaning it.
     *
     * @param text the raw text to be chunked
     * @return a list of clean, meaningful word chunks
     */
    public List<String> chunk(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String cleanedText = text.replaceAll("[^a-zA-Z0-9\\s]", " ");
        String[] words = cleanedText.split("\\s+");

        List<String> chunks = new ArrayList<>();
        for (String word : words) {
            String lowerWord = word.toLowerCase();
            if (!lowerWord.isEmpty() 
                    && !lowerWord.matches("\\d+") 
                    && !STOP_WORDS.contains(lowerWord)) {
                chunks.add(lowerWord);
            }
        }

        return chunks;
    }

    /**
     * Joins a list of chunks back into a single normalized string.
     *
     * @param chunks the list of word chunks
     * @return a single string with chunks separated by spaces
     */
    public String combineChunks(List<String> chunks) {
        if (chunks == null || chunks.isEmpty()) {
            return "";
        }
        return String.join(" ", chunks);
    }
}