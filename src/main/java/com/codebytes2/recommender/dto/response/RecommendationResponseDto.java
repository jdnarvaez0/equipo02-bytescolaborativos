package com.codebytes2.recommender.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para la respuesta de recomendaciones")
public class RecommendationResponseDto {

    @Schema(description = "ID de la recomendación", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID id;

    @Schema(description = "ID del usuario para el que se generaron las recomendaciones", example = "e89b158e-0000-1111-2222-123456789abc")
    private UUID userId;

    @Schema(description = "Lista de productos recomendados")
    private List<RecommendedProductDto> recommendedProducts;

    @Schema(description = "Fecha y hora de generación de las recomendaciones")
    private Instant computedAt;

    @Schema(description = "Versión del algoritmo utilizado", example = "v1.0")
    private String algorithmVersion;
}