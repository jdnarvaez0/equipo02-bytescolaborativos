package com.codebytes2.recommender.repository;

import com.codebytes2.recommender.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {
    List<Recommendation> findByUserId(UUID userId);
    
    /**
     * Finds the most recent recommendation for a specific user
     */
    Recommendation findTopByUserIdOrderByComputedAtDesc(UUID userId);
}