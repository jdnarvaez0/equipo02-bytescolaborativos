package com.codebytes2.recommender.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO for a rating response")
public class RatingResponseDto {

    @Schema(description = "Unique identifier of the rating", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID id;
    
    @Schema(description = "ID of the user who made the rating", example = "e89b158e-0000-1111-2222-123456789abc")
    private UUID userId;
    
    @Schema(description = "ID of the product that was rated", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8")
    private UUID productId;
    
    @Schema(description = "Score given to the product (1 to 5)", example = "4")
    private Integer score;
    
    @Schema(description = "Timestamp when the rating was created", example = "2025-11-18T12:00:00Z")
    private Instant createdAt;
}