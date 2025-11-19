package com.codebytes2.recommender.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para unirse a un torneo")
public class TournamentJoinRequest {

    @Schema(description = "Nickname del jugador para el torneo", example = "DevChPlayer", required = false)
    @NotBlank(message = "El nickname es obligatorio")
    private String nickname;
}