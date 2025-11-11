package com.codebytes2.recommender.dto;

import jakarta.validation.constraints.Future;

import java.time.Instant;

public record TournamentCreateRequest(
        String name,
        @Future Instant startDate,
        @Future Instant endDate,
        @Future Instant registrationOpenAt,
        @Future Instant registrationCloseAt,
        String game,
        String rules,
        int maxParticipants
) {
}
