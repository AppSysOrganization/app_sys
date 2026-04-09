package com.appointmentsystem.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the TextEmbedder class.
 *
 * @author Shahd
 * @version 1.0
 */
class TextEmbedderTest {

    /** The TextEmbedder instance used as a test fixture. */
    private TextEmbedder embedder;

    /** Sample documents used to train the embedder (fit phase). */
    private List<String> sampleDocuments;

    /**
     * Sets up the test environment before each test method.
     */
    @BeforeEach
    void setUp() {
        embedder = new TextEmbedder();
        sampleDocuments = Arrays.asList(
                "fast electric car",
                "cheap economic car",
                "luxury leather sofa"
        );
        embedder.fit(sampleDocuments);
    }

    /**
     * Tests the transform method to ensure it creates a vector of the correct size.
     */
    @Test
    void testTransformVectorSize() {
        double[] vector = embedder.transform("electric car");
        assertEquals(8, vector.length);
    }

    /**
     * Tests the cosine similarity between two identical vectors.
     */
    @Test
    void testCosineSimilarityIdenticalVectors() {
        double[] vectorA = embedder.transform("fast electric car");
        double[] vectorB = embedder.transform("fast electric car");
        
        double similarity = TextEmbedder.cosineSimilarity(vectorA, vectorB);
        
        assertEquals(1.0, similarity, 0.0001);
    }

    /**
     * Tests the cosine similarity between two completely different vectors.
     */
    @Test
    void testCosineSimilarityOrthogonalVectors() {
        double[] vectorCar = embedder.transform("fast electric car");
        double[] vectorSofa = embedder.transform("luxury leather sofa");
        
        double similarity = TextEmbedder.cosineSimilarity(vectorCar, vectorSofa);
        
        assertEquals(0.0, similarity, 0.0001);
    }

    /**
     * Tests the cosine similarity between partially similar vectors.
     */
    @Test
    void testCosineSimilarityPartialMatch() {
        double[] vectorA = embedder.transform("fast electric car");
        double[] vectorB = embedder.transform("cheap economic car");
        
        double similarity = TextEmbedder.cosineSimilarity(vectorA, vectorB);
        
        assertTrue(similarity > 0.0 && similarity < 1.0);
    }

    /**
     * Tests transforming a null input.
     */
    @Test
    void testTransformWithNullInput() {
        double[] vector = embedder.transform(null);
        
        assertEquals(8, vector.length);
        for (double v : vector) {
            assertEquals(0.0, v, 0.0001);
        }
    }

    /**
     * Tests transforming text containing words not present in the training vocabulary.
     */
    @Test
    void testTransformWithUnknownWords() {
        double[] vector = embedder.transform("unknown random words");
        
        assertEquals(8, vector.length);
        for (double v : vector) {
            assertEquals(0.0, v, 0.0001);
        }
    }

    /**
     * Tests cosine similarity with vectors of different lengths.
     */
    @Test
    void testCosineSimilarityWithDifferentLengths() {
        double[] vectorA = new double[]{1.0, 2.0};
        double[] vectorB = new double[]{1.0, 2.0, 3.0};
        
        double similarity = TextEmbedder.cosineSimilarity(vectorA, vectorB);
        
        assertEquals(0.0, similarity, 0.0001);
    }

    /**
     * Tests cosine similarity with empty vectors.
     */
    @Test
    void testCosineSimilarityWithEmptyVectors() {
        double[] vectorA = new double[0];
        double[] vectorB = new double[0];
        
        double similarity = TextEmbedder.cosineSimilarity(vectorA, vectorB);
        
        assertEquals(0.0, similarity, 0.0001);
    }
}