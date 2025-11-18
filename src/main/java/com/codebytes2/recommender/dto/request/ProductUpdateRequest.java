package com.codebytes2.recommender.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para actualizar un producto")
public class ProductUpdateRequest {

    @Schema(description = "Nombre del producto", example = "Counter-Strike 2", required = false)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no debe exceder los 255 caracteres")
    private String name;

    @Schema(description = "Descripción del producto", example = "Juego de disparos en primera persona", required = false)
    private String description;

    @Schema(description = "Categoría del producto", example = "FPS", required = false)
    private String category;

    @Schema(description = "Tags del producto", example = "[\"shooter\", \"competitive\", \"multiplayer\"]", required = false)
    private Set<String> tags;

    @Schema(description = "Puntuación de popularidad", example = "100", required = false)
    private Long popularityScore;
}