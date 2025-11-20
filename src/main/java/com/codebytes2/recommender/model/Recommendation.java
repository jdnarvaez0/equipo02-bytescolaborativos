package com.codebytes2.recommender.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "recommendations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recommendation {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ElementCollection
    @CollectionTable(name = "recommended_products", joinColumns = @JoinColumn(name = "recommendation_result_id"))
    @Column(name = "product_id")
    private List<UUID> productIds = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private Instant computedAt;

    @Column(nullable = false)
    private String algorithmVersion;

    @PrePersist
    protected void onCreate() {
        if (computedAt == null) {
            computedAt = Instant.now();
        }
        if (algorithmVersion == null) {
            algorithmVersion = "v1.0";
        }
    }
}
