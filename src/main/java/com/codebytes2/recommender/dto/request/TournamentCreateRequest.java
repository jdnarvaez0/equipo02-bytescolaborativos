package com.codebytes2.recommender.dto.request;

import java.time.Instant;

import com.codebytes2.recommender.backend.TournamentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO for creating a new tournament")
public class TournamentCreateRequest {

    @Schema(description = "Name of the tournament", example = "Torneo de Verano de Valorant")
    @NotBlank(message = "El nombre del torneo es obligatorio.")
    private String name;

    @Schema(description = "Game the tournament is for", example = "Valorant")
    @NotBlank(message = "El juego es obligatorio.")
    private String game;

    @Schema(description = "Date and time when the tournament starts (ISO 8601 format)", example = "2025-12-01T14:00:00Z")
    @NotNull(message = "La fecha de inicio es obligatoria.")
    @Future(message = "La fecha de inicio debe ser en el futuro.")
    private Instant startDate;

    @Schema(description = "Date and time when the tournament ends (ISO 8601 format)", example = "2025-12-03T20:00:00Z")
    @NotNull(message = "La fecha de finalización es obligatoria.")
    @Future(message = "La fecha de finalización debe ser posterior a la actual.")
    private Instant endDate;

    // NOTA: Para validar que endDate > startDate, se requeriría una anotación
    // a nivel de clase o una validación manual en el Service o Controller.

    @Schema(description = "Date and time when registration opens (ISO 8601 format)", example = "2025-11-20T00:00:00Z")
    @NotNull(message = "La fecha de apertura de registro es obligatoria.")
    @Future(message = "La apertura de registro debe ser en el futuro.")
    private Instant registrationOpenAt;

    @Schema(description = "Date and time when registration closes (ISO 8601 format)", example = "2025-11-30T00:00:00Z")
    @NotNull(message = "La fecha de cierre de registro es obligatoria.")
    @Future(message = "El cierre de registro debe ser en el futuro.")
    private Instant registrationCloseAt;

    @Schema(description = "Rules of the tournament", example = "Formato suizo, Bo3 en playoffs.")
    @NotBlank(message = "Las reglas del torneo son obligatorias.")
    private String rules;

    @Schema(description = "Maximum number of participants allowed", example = "32")
    @NotNull(message = "El número máximo de participantes es obligatorio.")
    @Min(value = 2, message = "El torneo debe tener al menos {value} participantes.")
    private Integer maxParticipants;

    // El estado inicial (status) podría ser opcional si el servicio lo fija por defecto
    // o se podría añadir @NotNull si se espera que el cliente lo envíe.
    @Schema(description = "Initial status of the tournament. If not provided, it's determined by the service.", example = "UPCOMING", allowableValues = {"UPCOMING", "OPEN", "CLOSED"})
    private TournamentStatus status;
}