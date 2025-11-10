package com.codebytes2.recommender.repository;

import com.codebytes2.recommender.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RecomendationRepository extends JpaRepository<Recommendation, UUID> {
    Optional<Recommendation> findTopByUserIdOrderByComputedAtDesc(UUID userId);
}
