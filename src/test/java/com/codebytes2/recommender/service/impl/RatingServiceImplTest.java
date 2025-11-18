package com.codebytes2.recommender.service.impl;

import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.dto.request.ProductRatingRequest;
import com.codebytes2.recommender.dto.response.RatingResponseDto;
import com.codebytes2.recommender.mapper.RatingMapper;
import com.codebytes2.recommender.model.Product;
import com.codebytes2.recommender.model.Rating;
import com.codebytes2.recommender.repository.ProductRepository;
import com.codebytes2.recommender.repository.RatingRepository;
import com.codebytes2.recommender.auth.repository.UserEntityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private UserEntityRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RatingMapper ratingMapper;

    private RatingServiceImpl ratingService;

    private UUID userId;
    private UUID productId;
    private UserEntity userEntity;
    private Product product;

    @BeforeEach
    void setUp() {
        ratingService = new RatingServiceImpl(ratingRepository, userRepository, productRepository, ratingMapper);
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();
        userEntity = new UserEntity();
        userEntity.setId(userId);
        product = new Product();
        product.setId(productId);
    }

    @Test
    void createRatingForAuthenticatedUser_ValidRequest_CreatesRating() {
        // Arrange
        Integer score = 4;
        ProductRatingRequest request = ProductRatingRequest.builder()
                .productId(productId)
                .score(score)
                .build();
        
        Rating expectedRating = new Rating();
        expectedRating.setScore(score);
        expectedRating.setUserEntity(userEntity);
        expectedRating.setProduct(product);
        
        RatingResponseDto expectedResponse = RatingResponseDto.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .productId(productId)
                .score(score)
                .build();
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(ratingRepository.existsByUserEntityIdAndProductId(userId, productId)).thenReturn(false);
        when(ratingMapper.toEntityFromRequestWithUserAndProduct(any(ProductRatingRequest.class), any(UserEntity.class), any(Product.class)))
                .thenReturn(expectedRating);
        when(ratingRepository.save(any(Rating.class))).thenReturn(expectedRating);
        when(ratingMapper.toResponseDto(any(Rating.class))).thenReturn(expectedResponse);

        // Act
        RatingResponseDto result = ratingService.createRatingForAuthenticatedUser(productId, score);

        // Assert
        assertNotNull(result);
        assertEquals(score, result.getScore());
        assertEquals(productId, result.getProductId());
        verify(ratingRepository).existsByUserEntityIdAndProductId(userId, productId);
        verify(ratingRepository).save(any(Rating.class));
        verify(ratingMapper).toResponseDto(any(Rating.class));
    }

    @Test
    void createRatingForAuthenticatedUser_ProductNotFound_ThrowsException() {
        // Arrange
        Integer score = 4;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> ratingService.createRatingForAuthenticatedUser(productId, score)
        );
        assertTrue(exception.getMessage().contains("Producto no encontrado"));
    }

    @Test
    void createRatingForAuthenticatedUser_DuplicateRating_ThrowsException() {
        // Arrange
        Integer score = 4;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(ratingRepository.existsByUserEntityIdAndProductId(userId, productId)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ratingService.createRatingForAuthenticatedUser(productId, score)
        );
        assertEquals("El usuario ya ha valorado este producto", exception.getMessage());
    }

    @Test
    void createRatingForAuthenticatedUser_ValidScore_SavesWithCorrectScore() {
        // Arrange
        Integer score = 5;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(ratingRepository.existsByUserEntityIdAndProductId(userId, productId)).thenReturn(false);
        
        Rating savedRating = new Rating();
        savedRating.setScore(score);
        savedRating.setUserEntity(userEntity);
        savedRating.setProduct(product);
        savedRating.setId(UUID.randomUUID());
        
        RatingResponseDto responseDto = RatingResponseDto.builder()
                .id(savedRating.getId())
                .userId(userId)
                .productId(productId)
                .score(score)
                .build();
        
        when(ratingMapper.toEntityFromRequestWithUserAndProduct(any(ProductRatingRequest.class), any(UserEntity.class), any(Product.class)))
                .thenReturn(savedRating);
        when(ratingRepository.save(any(Rating.class))).thenReturn(savedRating);
        when(ratingMapper.toResponseDto(any(Rating.class))).thenReturn(responseDto);

        // Act
        RatingResponseDto result = ratingService.createRatingForAuthenticatedUser(productId, score);

        // Assert
        assertEquals(score, result.getScore());
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void getAverageRatingByProduct_WithRatings_ReturnsCorrectAverage() {
        // Arrange
        Rating rating1 = new Rating();
        rating1.setScore(5);
        Rating rating2 = new Rating();
        rating2.setScore(3);
        Rating rating3 = new Rating();
        rating3.setScore(4);
        
        List<Rating> ratings = List.of(rating1, rating2, rating3);
        when(ratingRepository.findByProductId(productId)).thenReturn(ratings);

        // Act
        Double average = ratingService.getAverageRatingByProduct(productId);

        // Assert
        assertEquals(4.0, average); // (5+3+4)/3 = 4.0
    }

    @Test
    void getAverageRatingByProduct_NoRatings_ReturnsNull() {
        // Arrange
        when(ratingRepository.findByProductId(productId)).thenReturn(List.of());

        // Act
        Double average = ratingService.getAverageRatingByProduct(productId);

        // Assert
        assertNull(average);
    }
}