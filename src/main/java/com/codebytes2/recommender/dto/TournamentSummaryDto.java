package com.codebytes2.recommender.dto;

import com.codebytes2.recommender.backend.TournamentStatus;

public record TournamentSummaryDto(
        Long id,
        String name,
        TournamentStatus status,
        String game,
        int participants
) {
}
