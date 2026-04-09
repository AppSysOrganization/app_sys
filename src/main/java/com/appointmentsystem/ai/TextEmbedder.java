package com.appointmentsystem.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for converting text into numerical vectors (Embedding).
 * 
 * @author Shahd
 * @version 1.0
 */
public class TextEmbedder {

    /** The global vocabulary extracted from all product descriptions. */
    private List<String> vocabulary;

    /** A map storing the Inverse Document Frequency (IDF) score for each word. */
    private Map<String, Double> idfMap;

    /** The total number of documents (products) used to build the vocabulary. */
    private int corpusSize;

    /**
     * Constructs a new TextEmbedder.
     */
    public TextEmbedder() {
        this.vocabulary = new ArrayList<>();
        this.idfMap = new HashMap<>();
        this.corpusSize = 0;
    }

    /**
     * Builds the vocabulary and calculates IDF scores based on all available documents.
     *
     * @param documents a list of cleaned text documents
     */
    public void fit(List<String> documents) {
        Map<String, Integer> documentFrequency = new HashMap<>();
        corpusSize = documents.size();

        for (String doc : documents) {
            if (doc == null || doc.trim().isEmpty()) continue;
            
            String[] words = doc.split("\\s+");
            Map<String, Integer> uniqueWordsInDoc = new HashMap<>();
            
            for (String word : words) {
                if (!word.isEmpty()) {
                    uniqueWordsInDoc.put(word, 1);
                }
            }
            
            for (String word : uniqueWordsInDoc.keySet()) {
                documentFrequency.put(word, documentFrequency.getOrDefault(word, 0) + 1);
                if (!vocabulary.contains(word)) {
                    vocabulary.add(word);
                }
            }
        }

        for (Map.Entry<String, Integer> entry : documentFrequency.entrySet()) {
            double idf = Math.log((double) corpusSize / (entry.getValue() + 1)) + 1.0;
            idfMap.put(entry.getKey(), idf);
        }
    }

    /**
     * Converts a single text document into a numerical vector (array of doubles).
     *
     * @param text the cleaned text to convert
     * @return a double array representing the text as numbers
     */
    public double[] transform(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new double[vocabulary.size()];
        }

        String[] words = text.split("\\s+");
        Map<String, Integer> termFrequency = new HashMap<>();
        int totalWordsInText = words.length;

        for (String word : words) {
            if (!word.isEmpty()) {
                termFrequency.put(word, termFrequency.getOrDefault(word, 0) + 1);
            }
        }

        double[] vector = new double[vocabulary.size()];
        for (int i = 0; i < vocabulary.size(); i++) {
            String vocabWord = vocabulary.get(i);
            if (termFrequency.containsKey(vocabWord)) {
                double tf = (double) termFrequency.get(vocabWord) / totalWordsInText;
                double idf = idfMap.getOrDefault(vocabWord, 1.0);
                vector[i] = tf * idf;
            } else {
                vector[i] = 0.0;
            }
        }

        return vector;
    }

    /**
     * Calculates the Cosine Similarity between two numerical vectors.
     *
     * @param vectorA the first numerical vector
     * @param vectorB the second numerical vector
     * @return the cosine similarity score between 0.0 and 1.0
     */
    public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
        if (vectorA.length != vectorB.length || vectorA.length == 0) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}