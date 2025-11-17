package com.codebytes2.recommender.backend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(enumAsRef = true, description = "Estado del torneo")
public enum TournamentStatus {
    UPCOMING("UPCOMING"),
    OPEN("OPEN"),
    CLOSED("CLOSED");

    private final String value;

    TournamentStatus(String value) {
        this.value = value;
    }
}
