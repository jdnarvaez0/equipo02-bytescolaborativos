package com.codebytes2.recommender.dto.response;

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
@Schema(description = "Respuesta de inscripción a torneo")
public class TournamentJoinResponse {

    @Schema(description = "Mensaje de confirmación", example = "Inscripción completada")
    private String message;

    @Schema(description = "ID del torneo", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID tournamentId;

    @Schema(description = "ID del usuario", example = "c23fa1b2-c3d4-e5f6-7890-123456789abc")
    private UUID userId;

    @Schema(description = "Estado de la inscripción", example = "REGISTERED")
    private String status;
}