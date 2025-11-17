package com.codebytes2.recommender.controller;

import com.codebytes2.recommender.auth.commons.dto.response.ErrorResponse;
import com.codebytes2.recommender.backend.TournamentStatus;
import com.codebytes2.recommender.dto.request.TournamentCreateRequest;
import com.codebytes2.recommender.dto.response.TournamentDetailDto;
import com.codebytes2.recommender.dto.response.TournamentSummaryDto;
import com.codebytes2.recommender.service.TournamentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "🏆 Torneos", description = "Gestión de torneos (solo ADMIN puede crear/eliminar)")
@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService service;

    @Operation(
            summary = "Crear un nuevo torneo",
            description = "Solo accesible para usuarios con rol **ADMIN**.\n" +
                    "Valida campos obligatorios y que todas las fechas sean futuras (pero **no valida coherencia entre fechas**, ej: `startDate < endDate`).",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del torneo",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TournamentCreateRequest.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo válido",
                                    value = """
                        {
                          "name": "Torneo Regional CS2",
                          "game": "Counter-Strike 2",
                          "startDate": "2025-12-01T14:00:00Z",
                          "endDate": "2025-12-03T20:00:00Z",
                          "registrationOpenAt": "2025-11-20T00:00:00Z",
                          "registrationCloseAt": "2025-11-30T00:00:00Z",
                          "rules": "Bo3, mapas: Mirage, Inferno, Nuke",
                          "maxParticipants": 16
                        }
                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Torneo creado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TournamentSummaryDto.class),
                                    examples = @ExampleObject(
                                            name = "Respuesta exitosa",
                                            value = """
                            {
                              "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                              "name": "Torneo Regional CS2",
                              "game": "Counter-Strike 2",
                              "participants": 0
                            }
                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos inválidos (campos faltantes, fechas pasadas)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "Fecha pasada",
                                            value = """
                            {
                              "status": 400,
                              "message": "Error de validación",
                              "errors": {
                                "startDate": "La fecha de inicio debe ser en el futuro."
                              },
                              "timestamp": "2025-11-17T12:00:00"
                            }
                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (usuario no es ADMIN)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "Sin rol ADMIN",
                                            value = """
                            {
                              "status": 403,
                              "message": "Acceso denegado: No tienes los permisos necesarios para esta acción.",
                              "timestamp": "2025-11-17T12:00:00"
                            }
                            """
                                    )
                            )
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TournamentDetailDto> createTournament(@Valid @RequestBody TournamentCreateRequest request) {
        TournamentDetailDto created = service.createTournament(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Obtener detalle de un torneo",
            description = "Endpoint público. Devuelve información básica del torneo.",
            parameters = @Parameter(
                    name = "id",
                    description = "ID del torneo (UUID)",
                    required = true,
                    example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Torneo encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TournamentSummaryDto.class),
                                    examples = @ExampleObject(
                                            name = "Respuesta exitosa",
                                            value = """
                            {
                              "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                              "name": "Torneo Regional CS2",
                              "game": "Counter-Strike 2",
                              "participants": 5
                            }
                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Torneo no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "No encontrado",
                                            value = """
                            {
                              "status": 404,
                              "message": "No entity found for query",
                              "timestamp": "2025-11-17T12:00:00"
                            }
                            """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TournamentDetailDto> getTournamentDetail(@PathVariable UUID id) {
        TournamentDetailDto tournament = service.getTournamentById(id);
        return ResponseEntity.ok(tournament);
    }

    @Operation(
            summary = "Listar torneos por estado",
            description = "Endpoint público. Filtra torneos por estado (`UPCOMING`, `OPEN`, `CLOSED`).",
            parameters = {
                    @Parameter(
                            name = "status",
                            description = "Estado del torneo",
                            required = true,
                            example = "UPCOMING"
                    ),
                    @Parameter(name = "page", description = "Número de página (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Elementos por página", example = "10"),
                    @Parameter(name = "sort", description = "Criterio de ordenación", example = "startDate,asc")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista paginada de torneos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            type = "object",
                                            example = """
                            {
                              "content": [
                                {
                                  "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                                  "name": "Torneo CS2",
                                  "game": "Counter-Strike 2",
                                  "participants": 3
                                }
                              ],
                              "pageable": { "pageNumber": 0, "pageSize": 20 },
                              "totalElements": 1,
                              "totalPages": 1,
                              "last": true,
                              "first": true,
                              "size": 20,
                              "number": 0
                            }
                            """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<TournamentSummaryDto>> listByStatus(
            @PathVariable TournamentStatus status,
            @PageableDefault(size = 20, sort = "startDate") Pageable pageable) {
        Page<TournamentSummaryDto> tournaments = service.getTournamentsByStatus(status, pageable);
        return ResponseEntity.ok(tournaments);
    }

    @Operation(
            summary = "Buscar torneos por juego",
            description = "Endpoint público. Busca torneos cuyo campo `game` contenga el texto proporcionado (búsqueda parcial, no sensible a mayúsculas).",
            parameters = {
                    @Parameter(
                            name = "game",
                            description = "Nombre o parte del nombre del juego",
                            required = true,
                            example = "valorant"
                    ),
                    @Parameter(name = "page", description = "Número de página", example = "0"),
                    @Parameter(name = "size", description = "Elementos por página", example = "10")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista paginada de torneos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            type = "object",
                                            example = """
                            {
                              "content": [
                                {
                                  "id": "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8",
                                  "name": "Torneo LATAM",
                                  "game": "Valorant",
                                  "participants": 12
                                }
                              ],
                              "totalElements": 1
                            }
                            """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/game")
    public ResponseEntity<Page<TournamentSummaryDto>> searchByGame(
            @RequestParam String game,
            @PageableDefault(size = 20, sort = "startDate") Pageable pageable) {
        Page<TournamentSummaryDto> tournaments = service.searchTournamentsByGame(game, pageable);
        return ResponseEntity.ok(tournaments);
    }

    @Operation(
            summary = "Eliminar un torneo",
            description = "Solo accesible para usuarios con rol **ADMIN**.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(
                    name = "id",
                    description = "ID del torneo (UUID)",
                    required = true,
                    example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Torneo eliminado exitosamente"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (no es ADMIN)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Torneo no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTournament(@PathVariable UUID id) {
        service.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }
}