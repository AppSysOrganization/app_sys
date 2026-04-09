package com.appointmentsystem.ai;

import com.appointmentsystem.model.Product;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Represents the core search engine of the simplified RAG System.
 *
 * @author Rahaf
 * @version 1.0
 */
public class ProductSearchEngine {

    /** The repository holding the real products data. */
    private final ProductRepository repository;

    /** The chunker used to split text into small pieces. */
    private final DocumentChunker chunker;

    /** The embedder used to convert text to numerical vectors. */
    private final TextEmbedder embedder;

    /** Stores the products that have been indexed for searching. */
    private List<Product> indexedProducts;

    /** Stores the numerical vectors corresponding to the indexed products. */
    private List<double[]> indexedVectors;

    /** Keywords related to cars category. */
    /** Keywords related to cars category. */
    private static final List<String> CAR_KEYWORDS = Arrays.asList(
            "car", "vehicle", "auto", "automotive", "sedan", "suv", "truck",
            "toyota", "honda", "bmw", "mercedes", "ford", "hyundai", "kia",
            "engine", "drive", "fuel", "electric", "hybrid",
            "sport", "family", "economic", "luxury car", "sport car"
    );

    /** Keywords related to furniture category. */
    private static final List<String> FURNITURE_KEYWORDS = Arrays.asList(
            "sofa", "chair", "table", "bed", "desk", "cabinet", "shelf",
            "couch", "furniture", "living room", "bedroom", "office",
            "wooden", "fabric", "leather", "dining", "wardrobe"
    );

    /** The boost score added when category matches. */
    private static final double CATEGORY_BOOST = 0.3;

    /**
     * Constructs a new ProductSearchEngine.
     *
     * @param repository the data source containing all products
     */
    public ProductSearchEngine(ProductRepository repository) {
        this.repository = repository;
        this.chunker = new DocumentChunker();
        this.embedder = new TextEmbedder();
        this.indexedProducts = new ArrayList<>();
        this.indexedVectors = new ArrayList<>();
        buildIndex();
    }

    /**
     * Builds the search index by processing all products in the repository.
     */
    private void buildIndex() {
        indexedProducts = repository.getAllProducts();
        if (indexedProducts.isEmpty()) {
            return;
        }

        List<String> documentTexts = new ArrayList<>();

        for (Product product : indexedProducts) {
            String fullText = product.getName() + " " + product.getCategory() + " " + product.getDescription();
            List<String> chunks = chunker.chunk(fullText);
            String cleanText = chunker.combineChunks(chunks);
            documentTexts.add(cleanText);
        }

        embedder.fit(documentTexts);

        for (String cleanText : documentTexts) {
            double[] vector = embedder.transform(cleanText);
            indexedVectors.add(vector);
        }
    }

    /**
     * Detects the category implied by the user's query.
     *
     * @param query the cleaned query text
     * @return "car" if car-related, "furniture" if furniture-related, "unknown" otherwise
     */
    private String detectQueryCategory(String query) {
        String lowerQuery = query.toLowerCase();
        int carScore = 0;
        int furnitureScore = 0;

        for (String keyword : CAR_KEYWORDS) {
            if (lowerQuery.contains(keyword)) carScore++;
        }

        for (String keyword : FURNITURE_KEYWORDS) {
            if (lowerQuery.contains(keyword)) furnitureScore++;
        }

        if (carScore > furnitureScore) return "car";
        if (furnitureScore > carScore) return "furniture";
        return "unknown";
    }

    /**
     * Checks if a product belongs to the detected category.
     *
     * @param product         the product to check
     * @param detectedCategory the category detected from the query
     * @return true if the product matches the category
     */
    private boolean productMatchesCategory(Product product, String detectedCategory) {
        if (detectedCategory.equals("unknown")) return false;

        String productText = (product.getName() + " " + product.getCategory() + " " + product.getDescription()).toLowerCase();

        if (detectedCategory.equals("car")) {
            for (String keyword : CAR_KEYWORDS) {
                if (productText.contains(keyword)) return true;
            }
        }

        if (detectedCategory.equals("furniture")) {
            for (String keyword : FURNITURE_KEYWORDS) {
                if (productText.contains(keyword)) return true;
            }
        }

        return false;
    }

    /**
     * Searches for products that match the user's query.
     *
     * @param query the natural language query from the user
     * @param topN  the maximum number of recommended products to return
     * @return a list of top matching products, sorted by similarity
     */
    public List<Product> search(String query, int topN) {
        if (query == null || query.trim().isEmpty() || indexedProducts.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> queryChunks = chunker.chunk(query);
        String cleanQuery = chunker.combineChunks(queryChunks);
        double[] queryVector = embedder.transform(cleanQuery);

        String detectedCategory = detectQueryCategory(query);

        List<SimilarityResult> results = new ArrayList<>();
        for (int i = 0; i < indexedProducts.size(); i++) {
            double score = TextEmbedder.cosineSimilarity(queryVector, indexedVectors.get(i));

            if (productMatchesCategory(indexedProducts.get(i), detectedCategory)) {
                score += CATEGORY_BOOST;
            } else if (!detectedCategory.equals("unknown")) {
                score -= CATEGORY_BOOST;
            }

            if (score > 0.0) {
                results.add(new SimilarityResult(indexedProducts.get(i), score));
            }
        }

        results.sort(Comparator.comparingDouble(SimilarityResult::getScore).reversed());

        List<Product> recommendedProducts = new ArrayList<>();
        int limit = Math.min(topN, results.size());
        for (int i = 0; i < limit; i++) {
            recommendedProducts.add(results.get(i).getProduct());
        }

        return recommendedProducts;
    }

    /**
     * A helper inner class to hold a product and its calculated similarity score.
     */
    private static class SimilarityResult {

        /** The matched product. */
        private final Product product;

        /** The cosine similarity score. */
        private final double score;

        /**
         * Constructs a SimilarityResult.
         *
         * @param product the matched product
         * @param score   the cosine similarity score
         */
        public SimilarityResult(Product product, double score) {
            this.product = product;
            this.score = score;
        }

        /**
         * Gets the product.
         *
         * @return the product
         */
        public Product getProduct() {
            return product;
        }

        /**
         * Gets the similarity score.
         *
         * @return the score
         */
        public double getScore() {
            return score;
        }
    }
}