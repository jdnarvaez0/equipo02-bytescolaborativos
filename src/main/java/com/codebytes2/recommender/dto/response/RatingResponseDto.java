package com.codebytes2.recommender.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDto {

    private UUID id;
    
    private UUID userId;
    
    private UUID productId;
    
    private Integer score;
    
    private Instant createdAt;
}