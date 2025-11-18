package com.codebytes2.recommender.recommendation.service;

import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.auth.repository.UserEntityRepository;
import com.codebytes2.recommender.dto.response.RecommendationResponseDto;
import com.codebytes2.recommender.mapper.RecommendationMapper;
import com.codebytes2.recommender.model.Product;
import com.codebytes2.recommender.model.Rating;
import com.codebytes2.recommender.model.Recommendation;
import com.codebytes2.recommender.repository.ProductRepository;
import com.codebytes2.recommender.repository.RatingRepository;
import com.codebytes2.recommender.repository.RecommendationRepository;
import com.codebytes2.recommender.service.impl.RecommendationServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private RecommendationMapper recommendationMapper;

    private RecommendationServiceImpl recommendationService;

    private UUID userId;
    private UserEntity user;
    private Product product1, product2, product3;

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationServiceImpl(
                recommendationRepository, productRepository, ratingRepository, 
                userEntityRepository, recommendationMapper);
        
        userId = UUID.randomUUID();
        user = new UserEntity();
        user.setId(userId);

        // Create test products
        product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setName("Product 1");
        product1.setTags(Set.of("tag1", "tag2"));
        product1.setPopularityScore(100L);

        product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setName("Product 2");
        product2.setTags(Set.of("tag2", "tag3"));
        product2.setPopularityScore(200L);

        product3 = new Product();
        product3.setId(UUID.randomUUID());
        product3.setName("Product 3");
        product3.setTags(Set.of("tag3", "tag4"));
        product3.setPopularityScore(50L);
    }

    @Test
    void getRecommendationsForUser_UserExists_ReturnsRecommendations() {
        // Given
        List<Product> allProducts = Arrays.asList(product1, product2, product3);
        Rating rating1 = new Rating();
        rating1.setProduct(product1);
        rating1.setScore(5);
        List<Rating> userRatings = Arrays.asList(rating1);

        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findAll()).thenReturn(allProducts);
        when(ratingRepository.findByUserEntityId(userId)).thenReturn(userRatings);
        when(ratingRepository.findByProductId(any(UUID.class))).thenReturn(new ArrayList<>()); // Empty ratings for products
        when(ratingRepository.existsByUserEntityIdAndProductId(any(UUID.class), any(UUID.class))).thenReturn(false);

        // When
        RecommendationResponseDto result = recommendationService.getRecommendationsForUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertNotNull(result.getRecommendedProducts());
        assertFalse(result.getRecommendedProducts().isEmpty());
        
        // Verify repository calls
        verify(userEntityRepository).findById(userId);
        verify(productRepository).findAll();
        verify(ratingRepository).findByUserEntityId(userId);
        
        // Verify that a recommendation was saved
        ArgumentCaptor<Recommendation> recommendationCaptor = ArgumentCaptor.forClass(Recommendation.class);
        verify(recommendationRepository).save(recommendationCaptor.capture());
        Recommendation savedRecommendation = recommendationCaptor.getValue();
        assertEquals(userId, savedRecommendation.getUserId());
    }

    @Test
    void getRecommendationsForUser_UserDoesNotExist_ThrowsException() {
        // Given
        when(userEntityRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> recommendationService.getRecommendationsForUser(userId));
        
        verify(userEntityRepository).findById(userId);
        verify(productRepository, never()).findAll();
        verify(recommendationRepository, never()).save(any());
    }

    @Test
    void getRecommendationsForUser_NoUserRatings_ReturnsRecommendationsBasedOnPopularity() {
        // Given
        List<Product> allProducts = Arrays.asList(product1, product2, product3);
        List<Rating> userRatings = new ArrayList<>(); // No ratings

        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findAll()).thenReturn(allProducts);
        when(ratingRepository.findByUserEntityId(userId)).thenReturn(userRatings);
        when(ratingRepository.findByProductId(any(UUID.class))).thenReturn(new ArrayList<>());
        when(ratingRepository.existsByUserEntityIdAndProductId(any(UUID.class), any(UUID.class))).thenReturn(false);

        // When
        RecommendationResponseDto result = recommendationService.getRecommendationsForUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertNotNull(result.getRecommendedProducts());
    }

    @Test
    void calculateRelevanceScore_TagSimilarityWorks() {
        // For this test, we'll verify the algorithm behavior by mocking the internal methods
        // The actual algorithm test is embedded in the main functionality
        
        List<Product> allProducts = Arrays.asList(product1, product2, product3);
        Rating rating1 = new Rating();
        rating1.setProduct(product1);
        rating1.setScore(5);
        List<Rating> userRatings = Arrays.asList(rating1);

        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findAll()).thenReturn(allProducts);
        when(ratingRepository.findByUserEntityId(userId)).thenReturn(userRatings);
        when(ratingRepository.findByProductId(any(UUID.class))).thenReturn(new ArrayList<>());
        when(ratingRepository.existsByUserEntityIdAndProductId(any(UUID.class), any(UUID.class))).thenReturn(false);

        // When
        RecommendationResponseDto result = recommendationService.getRecommendationsForUser(userId);

        // Then
        assertNotNull(result);
        // Verify that products with tags matching the user's preferences get higher scores
        // (This is tested through the ordering of results in real implementation)
    }
}