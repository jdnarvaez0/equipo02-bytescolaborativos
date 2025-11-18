package com.codebytes2.recommender.controller;

import com.codebytes2.recommender.dto.response.RecommendationResponseDto;
import com.codebytes2.recommender.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "💡 Recommendations", description = "Sistema de recomendación de productos")
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(
            summary = "Obtener recomendaciones para un usuario",
            description = "Devuelve una lista de productos recomendados para un usuario basados en sus preferencias pasadas (tags, valoraciones), popularidad y similitud de contenido.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "ID del usuario para el que se generan las recomendaciones",
                            required = true,
                            example = "e89b158e-0000-1111-2222-123456789abc",
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer token para autenticación",
                            in = ParameterIn.HEADER,
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Recomendaciones obtenidas exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RecommendationResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "Respuesta de recomendaciones",
                                            value = """
                            {
                              "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                              "userId": "e89b158e-0000-1111-2222-123456789abc",
                              "recommendedProducts": [
                                {
                                  "id": "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8",
                                  "name": "Counter-Strike 2",
                                  "description": "Juego de disparos en primera persona",
                                  "category": "FPS",
                                  "tags": ["shooter", "competitive", "multiplayer"],
                                  "averageRating": 4.2,
                                  "popularityScore": 950,
                                  "relevanceScore": 0.85
                                }
                              ],
                              "computedAt": "2025-11-18T12:00:00Z",
                              "algorithmVersion": "v1.0"
                            }
                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = com.codebytes2.recommender.auth.commons.dto.response.ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = com.codebytes2.recommender.auth.commons.dto.response.ErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/{userId}")
    public ResponseEntity<RecommendationResponseDto> getRecommendations(@PathVariable UUID userId) {
        RecommendationResponseDto recommendations = recommendationService.getRecommendationsForUser(userId);
        return ResponseEntity.ok(recommendations);
    }
}