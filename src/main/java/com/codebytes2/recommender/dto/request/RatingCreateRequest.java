package com.codebytes2.recommender.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for creating a rating, typically used internally or by admin roles")
public class RatingCreateRequest {

    @Schema(description = "ID of the user making the rating (usually set by the system from authentication)", example = "e89b158e-0000-1111-2222-123456789abc", nullable = true)
    private UUID userId; // Optional, will be set by the controller from authentication

    @Schema(description = "ID of the product being rated", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8")
    @NotNull(message = "El ID de producto es obligatorio")
    private UUID productId;

    @Schema(description = "Rating score for the product (1 to 5)", example = "5")
    @NotNull(message = "La puntuación es obligatoria")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    private Integer score;
}