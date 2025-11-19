package com.codebytes2.recommender.repository;

import com.codebytes2.recommender.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
    List<Rating> findByUserEntityId(UUID userId);
    List<Rating> findByProductId(UUID productId);
    boolean existsByUserEntityIdAndProductId(UUID userId, UUID productId);
    long countByProductId(UUID productId);
}
