package com.codebytes2.recommender.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para un producto en respuesta detallada")
public class ProductDetailDto {

    @Schema(description = "ID del producto", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID id;

    @Schema(description = "Nombre del producto", example = "Counter-Strike 2")
    private String name;

    @Schema(description = "Descripción del producto", example = "Juego de disparos en primera persona")
    private String description;

    @Schema(description = "Categoría del producto", example = "FPS")
    private String category;

    @Schema(description = "Tags del producto", example = "[\"shooter\", \"competitive\", \"multiplayer\"]")
    private Set<String> tags;

    @Schema(description = "Puntuación de popularidad", example = "100")
    private Long popularityScore;

    @Schema(description = "Fecha de creación", example = "2025-05-01T12:00:00Z")
    private Instant createdAt;
}