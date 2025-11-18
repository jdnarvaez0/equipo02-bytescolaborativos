package com.codebytes2.recommender.service.impl;

import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.auth.repository.UserEntityRepository;
import com.codebytes2.recommender.dto.response.RecommendationResponseDto;
import com.codebytes2.recommender.dto.response.RecommendedProductDto;
import com.codebytes2.recommender.mapper.RecommendationMapper;
import com.codebytes2.recommender.model.Product;
import com.codebytes2.recommender.model.Rating;
import com.codebytes2.recommender.model.Recommendation;
import com.codebytes2.recommender.repository.ProductRepository;
import com.codebytes2.recommender.repository.RatingRepository;
import com.codebytes2.recommender.repository.RecommendationRepository;
import com.codebytes2.recommender.service.RecommendationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final ProductRepository productRepository;
    private final RatingRepository ratingRepository;
    private final UserEntityRepository userEntityRepository;
    private final RecommendationMapper recommendationMapper;

    @Override
    public RecommendationResponseDto getRecommendationsForUser(UUID userId) {
        // Verify user exists
        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + userId));

        // Get all products
        List<Product> allProducts = productRepository.findAll();
        
        // Get user's ratings to determine their preferences
        List<Rating> userRatings = ratingRepository.findByUserEntityId(userId);
        
        // Calculate recommendations based on tags, ratings, and popularity
        List<RecommendedProductDto> recommendedProducts = calculateRecommendations(userId, allProducts, userRatings);
        
        // Create and save the recommendation record
        Recommendation recommendation = new Recommendation();
        recommendation.setUserId(userId);
        recommendation.setProductIds(recommendedProducts.stream()
                .map(RecommendedProductDto::getId)
                .collect(Collectors.toList()));
        recommendation.setComputedAt(Instant.now());
        recommendation.setAlgorithmVersion("v1.0");
        
        recommendationRepository.save(recommendation);

        // Build response DTO
        return RecommendationResponseDto.builder()
                .id(recommendation.getId())
                .userId(userId)
                .recommendedProducts(recommendedProducts)
                .computedAt(recommendation.getComputedAt())
                .algorithmVersion(recommendation.getAlgorithmVersion())
                .build();
    }

    private List<RecommendedProductDto> calculateRecommendations(UUID userId, List<Product> products, List<Rating> userRatings) {
        // Get user's favorite tags based on their ratings
        Map<String, Double> userTagPreferences = getUserTagPreferences(userRatings);
        
        // Calculate relevance score for each product
        List<ScoredProduct> scoredProducts = products.stream()
                .map(product -> {
                    double relevanceScore = calculateRelevanceScore(userId, product, userTagPreferences, userRatings);
                    return new ScoredProduct(product, relevanceScore);
                })
                .sorted((a, b) -> Double.compare(b.score, a.score)) // Sort by score descending
                .collect(Collectors.toList());

        // Convert to DTOs with their relevance scores
        return scoredProducts.stream()
                .map(scoredProduct -> {
                    RecommendedProductDto dto = RecommendedProductDto.fromProduct(scoredProduct.product);
                    dto.setRelevanceScore(scoredProduct.score);
                    
                    // Add average rating to the DTO
                    List<Rating> productRatings = ratingRepository.findByProductId(scoredProduct.product.getId());
                    if (!productRatings.isEmpty()) {
                        double avgRating = productRatings.stream()
                                .mapToInt(Rating::getScore)
                                .average()
                                .orElse(0.0);
                        dto.setAverageRating(avgRating);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private Map<String, Double> getUserTagPreferences(List<Rating> userRatings) {
        Map<String, Double> tagPreferences = new HashMap<>();
        
        for (Rating rating : userRatings) {
            // Get the product for this rating
            Product product = rating.getProduct();
            if (product != null && product.getTags() != null) {
                // Weight tags by the rating score given by the user
                for (String tag : product.getTags()) {
                    // Normalize rating score from 1-5 to 0-1 scale first, then multiply by tag weight
                    double currentWeight = tagPreferences.getOrDefault(tag, 0.0);
                    double tagWeight = (rating.getScore() / 5.0); // Convert rating (1-5) to (0.2-1.0)
                    tagPreferences.put(tag, currentWeight + tagWeight);
                }
            }
        }
        
        return tagPreferences;
    }

    private double calculateRelevanceScore(UUID userId, Product product, Map<String, Double> userTagPreferences, List<Rating> userRatings) {
        double tagScore = calculateTagScore(product, userTagPreferences);
        double ratingScore = calculateRatingScore(product, userRatings);
        double popularityScore = calculatePopularityScore(product);
        
        // Weight the different scores (these weights can be adjusted as needed)
        double finalScore = (tagScore * 0.5) + (ratingScore * 0.3) + (popularityScore * 0.2);
        
        // Avoid recommending products the user has already rated highly
        if (userHasRatedProduct(userId, product.getId())) {
            finalScore = 0; // Don't recommend products the user has already rated
        }
        
        return finalScore;
    }

    private double calculateTagScore(Product product, Map<String, Double> userTagPreferences) {
        if (product.getTags() == null || userTagPreferences.isEmpty()) {
            return 0.0;
        }

        double score = 0.0;
        for (String tag : product.getTags()) {
            score += userTagPreferences.getOrDefault(tag, 0.0);
        }
        
        return Math.min(score, 10.0); // Cap the tag score to prevent it from being too dominant
    }

    private double calculateRatingScore(Product product, List<Rating> userRatings) {
        // Get average rating for this product
        List<Rating> productRatings = ratingRepository.findByProductId(product.getId());
        if (productRatings.isEmpty()) {
            return 0.0; // No ratings for this product
        }
        
        double averageRating = productRatings.stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0);
                
        // Normalize from 1-5 scale to 0-1 scale
        return (averageRating - 1) / 4; // Converts 1-5 to 0-1
    }

    private double calculatePopularityScore(Product product) {
        // Normalize popularity score (assuming larger popularity scores are better)
        // Using logarithmic scale to prevent very popular items from dominating
        if (product.getPopularityScore() == null) {
            return 0.0;
        }
        
        // Log scale to normalize popularity differences
        double normalizedPopularity = Math.log(1 + product.getPopularityScore()) / 10.0;
        return Math.min(normalizedPopularity, 1.0); // Cap at 1.0
    }

    private boolean userHasRatedProduct(UUID userId, UUID productId) {
        return ratingRepository.existsByUserEntityIdAndProductId(userId, productId);
    }

    // Helper class to hold product with its calculated score
    private static class ScoredProduct {
        final Product product;
        final double score;

        ScoredProduct(Product product, double score) {
            this.product = product;
            this.score = score;
        }
    }
}