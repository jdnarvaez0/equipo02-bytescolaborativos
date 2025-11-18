package com.codebytes2.recommender.dto.response;

import com.codebytes2.recommender.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para un producto recomendado")
public class RecommendedProductDto {

    @Schema(description = "ID del producto recomendado", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8")
    private UUID id;

    @Schema(description = "Nombre del producto", example = "Counter-Strike 2")
    private String name;

    @Schema(description = "Descripción del producto", example = "Juego de disparos en primera persona")
    private String description;

    @Schema(description = "Categoría del producto", example = "FPS")
    private String category;

    @Schema(description = "Tags del producto", example = "[\"shooter\", \"competitive\", \"multiplayer\"]")
    private java.util.Set<String> tags;

    @Schema(description = "Puntuación promedio de valoraciones", example = "4.2")
    private Double averageRating;

    @Schema(description = "Puntuación de popularidad", example = "950")
    private Long popularityScore;

    @Schema(description = "Puntuación de relevancia para el usuario", example = "0.85")
    private Double relevanceScore;

    public static RecommendedProductDto fromProduct(Product product) {
        return RecommendedProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .tags(product.getTags())
                .popularityScore(product.getPopularityScore())
                .build();
    }
}