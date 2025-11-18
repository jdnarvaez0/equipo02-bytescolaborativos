package com.codebytes2.recommender.service;

import com.codebytes2.recommender.dto.response.RecommendationResponseDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface RecommendationService {
    RecommendationResponseDto getRecommendationsForUser(UUID userId);
}