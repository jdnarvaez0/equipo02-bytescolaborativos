package com.codebytes2.recommender.service.impl;

import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.auth.repository.UserEntityRepository;
import com.codebytes2.recommender.dto.request.RatingCreateRequest;
import com.codebytes2.recommender.dto.request.ProductRatingRequest;
import com.codebytes2.recommender.dto.response.RatingResponseDto;
import com.codebytes2.recommender.exceptions.DuplicateRatingException;
import com.codebytes2.recommender.mapper.RatingMapper;
import com.codebytes2.recommender.model.Product;
import com.codebytes2.recommender.model.Rating;
import com.codebytes2.recommender.repository.ProductRepository;
import com.codebytes2.recommender.repository.RatingRepository;
import com.codebytes2.recommender.service.RatingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final UserEntityRepository userRepository;
    private final ProductRepository productRepository;
    private final RatingMapper ratingMapper;

    @Override
    public RatingResponseDto createRating(RatingCreateRequest request) {
        // Validate if user exists
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + request.getUserId()));

        // Validate if product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Producto no encontrado con ID: " + request.getProductId()));

        // Check if user has already rated this product
        if (ratingRepository.existsByUserEntityIdAndProductId(user.getId(), product.getId())) {
            throw new DuplicateRatingException("El usuario ya ha valorado este producto");
        }

        // Create new rating using mapper
        ProductRatingRequest ratingRequest = ProductRatingRequest.builder()
                .productId(request.getProductId())
                .score(request.getScore())
                .build();

        Rating rating = ratingMapper.toEntityFromRequestWithUserAndProduct(ratingRequest, user, product);

        Rating savedRating = ratingRepository.save(rating);
        return ratingMapper.toResponseDto(savedRating);
    }

    @Override
    public RatingResponseDto createRatingByUser(UUID userId, UUID productId, Integer score) {
        // Validate if user exists
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + userId));

        // Validate if product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + productId));

        // Check if user has already rated this product
        if (ratingRepository.existsByUserEntityIdAndProductId(user.getId(), product.getId())) {
            throw new DuplicateRatingException("El usuario ya ha valorado este producto");
        }

        // Create new rating
        Rating rating = new Rating();
        rating.setUserEntity(user);
        rating.setProduct(product);
        rating.setScore(score);

        Rating savedRating = ratingRepository.save(rating);
        return ratingMapper.toResponseDto(savedRating);
    }

    @Override
    public Double getAverageRatingByProduct(UUID productId) {
        // Get all ratings for the product
        var ratings = ratingRepository.findByProductId(productId);
        if (ratings.isEmpty()) {
            return null; // No ratings for this product
        }

        // Calculate average
        return ratings.stream()
                .mapToDouble(Rating::getScore)
                .average()
                .orElse(0.0);
    }

    @Override
    public long getRatingCountByProduct(UUID productId) {
        return ratingRepository.countByProductId(productId);
    }

    @Override
    public boolean userHasRatedProduct(UUID userId, UUID productId) {
        return ratingRepository.existsByUserEntityIdAndProductId(userId, productId);
    }
}