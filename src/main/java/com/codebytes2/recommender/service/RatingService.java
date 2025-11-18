package com.codebytes2.recommender.service;

import com.codebytes2.recommender.dto.request.RatingCreateRequest;
import com.codebytes2.recommender.dto.request.ProductRatingRequest;
import com.codebytes2.recommender.dto.response.RatingResponseDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface RatingService {

    RatingResponseDto createRating(RatingCreateRequest request);

    RatingResponseDto createRatingForAuthenticatedUser(UUID productId, Integer score);

    Double getAverageRatingByProduct(UUID productId);

    long getRatingCountByProduct(UUID productId);

    boolean userHasRatedProduct(UUID userId, UUID productId);
}