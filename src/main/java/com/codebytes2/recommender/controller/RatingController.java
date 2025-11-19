package com.codebytes2.recommender.controller;

import com.codebytes2.recommender.auth.commons.dto.response.ErrorResponse;
import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.dto.request.ProductRatingRequest;
import com.codebytes2.recommender.dto.response.RatingResponseDto;
import com.codebytes2.recommender.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Tag(name = "⭐ Ratings", description = "Gestión de valoraciones de productos")
@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @Operation(
            summary = "Valorar un producto",
            description = "Permite a un jugador valorar un producto con una puntuación entre 1 y 5. " +
                    "Requiere autenticación como **PLAYER** o **ADMIN**. " +
                    "El ID del usuario se obtiene automáticamente del token de autenticación. " +
                    "Un usuario no puede valorar el mismo producto dos veces.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la valoración",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductRatingRequest.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de valoración",
                                    value = """
                        {
                          "productId": "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8",
                          "score": 4
                        }
                        """
                            )
                    )
            ),
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer token para autenticación",
                            in = ParameterIn.HEADER,
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Valoración creada exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RatingResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "Respuesta exitosa",
                                            value = """
                            {
                              "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                              "userId": "e89b158e-0000-1111-2222-123456789abc",
                              "productId": "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8",
                              "score": 4,
                              "createdAt": "2025-11-18T12:00:00Z"
                            }
                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos inválidos o usuario ya ha valorado este producto",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = com.codebytes2.recommender.auth.commons.dto.response.ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado - No tiene rol PLAYER o ADMIN"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Producto no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = com.codebytes2.recommender.auth.commons.dto.response.ErrorResponse.class)
                            )
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('PLAYER') or hasRole('ADMIN')")
    public ResponseEntity<RatingResponseDto> createRating(@Valid @RequestBody ProductRatingRequest request) {
        // Get the authenticated user ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity authenticatedUser = (UserEntity) authentication.getPrincipal();

        RatingResponseDto rating = ratingService.createRatingByUser(authenticatedUser.getId(), request.getProductId(), request.getScore());
        return ResponseEntity.status(HttpStatus.CREATED).body(rating);
    }

    @Operation(
            summary = "Obtener promedio de valoraciones de un producto",
            description = "Devuelve la puntuación promedio de un producto basada en todas las valoraciones recibidas.",
            parameters = @Parameter(
                    name = "productId",
                    description = "ID del producto",
                    required = true,
                    example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Promedio de valoraciones obtenido exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = "Respuesta exitosa",
                                            value = """
                            {
                              "averageRating": 4.2,
                              "totalRatings": 5
                            }
                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Producto no encontrado o sin valoraciones",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = com.codebytes2.recommender.auth.commons.dto.response.ErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/average/{productId}")
    public ResponseEntity<Map<String, Object>> getAverageRating(@PathVariable UUID productId) {
        Double averageRating = ratingService.getAverageRatingByProduct(productId);
        long totalRatings = ratingService.getRatingCountByProduct(productId);

        if (totalRatings == 0) {
            // If no ratings exist, return a default response
            Map<String, Object> response = Map.of(
                    "averageRating", 0.0,
                    "totalRatings", 0
            );
            return ResponseEntity.ok(response);
        }

        Map<String, Object> response = Map.of(
                "averageRating", averageRating,
                "totalRatings", totalRatings
        );
        return ResponseEntity.ok(response);
    }
}