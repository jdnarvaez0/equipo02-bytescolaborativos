package com.codebytes2.recommender.dto;

import com.codebytes2.recommender.backend.TournamentStatus;

import java.time.Instant;
import java.util.List;

public record TournamentDetailDto(
        Long id,
        String name,
        TournamentStatus status,
        String game,
        String rules,
        List<String> participants,
        int maxParticipants,
        Instant startDate,
        Instant endDate,
        Instant registrationOpenAt,
        Instant registrationCloseAt,
        Instant createdAt
) {
}
