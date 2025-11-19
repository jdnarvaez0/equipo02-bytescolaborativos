package com.codebytes2.recommender.dto.response;

import com.codebytes2.recommender.backend.TournamentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "DTO with a summary of tournament information")
public class TournamentSummaryDto {
    @Schema(description = "Unique identifier of the tournament", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8")
    private UUID id;

    @Schema(description = "Name of the tournament", example = "Torneo de Verano")
    private String name;

    @Schema(description = "Game of the tournament", example = "Valorant")
    private String game;

    @Schema(description = "Current status of the tournament", example = "UPCOMING")
    private TournamentStatus status;

    @Schema(description = "Current number of registered participants", example = "5")
    private Integer participants;
}