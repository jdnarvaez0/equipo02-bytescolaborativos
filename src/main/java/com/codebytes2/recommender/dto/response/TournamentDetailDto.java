package com.codebytes2.recommender.dto.response;

import com.codebytes2.recommender.backend.TournamentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Schema(description = "DTO with detailed information about a tournament")
public class TournamentDetailDto {
    @Schema(description = "Unique identifier of the tournament", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8")
    private UUID id;

    @Schema(description = "Name of the tournament", example = "Torneo Regional de CS2")
    private String name;

    @Schema(description = "Game of the tournament", example = "Counter-Strike 2")
    private String game;

    @Schema(description = "Rules of the tournament", example = "Bo3, mapas: Mirage, Inferno, Nuke")
    private String rules;

    @Schema(description = "Current status of the tournament", example = "OPEN")
    private TournamentStatus status;

    @Schema(description = "Start date and time of the tournament", example = "2025-12-01T14:00:00Z")
    private Instant startDate;

    @Schema(description = "End date and time of the tournament", example = "2025-12-03T20:00:00Z")
    private Instant endDate;

    @Schema(description = "Registration opening date and time", example = "2025-11-20T00:00:00Z")
    private Instant registrationOpenAt;

    @Schema(description = "Registration closing date and time", example = "2025-11-30T00:00:00Z")
    private Instant registrationCloseAt;

    @Schema(description = "Maximum number of participants", example = "16")
    private Integer maxParticipants;

    @Schema(description = "Current number of registered participants", example = "10")
    private Integer participants;
}