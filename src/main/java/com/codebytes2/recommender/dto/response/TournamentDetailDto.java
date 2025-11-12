package com.codebytes2.recommender.dto.response;

import com.codebytes2.recommender.backend.TournamentStatus;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;


@Data
public class TournamentDetailDto {
    private UUID id;
    private String name;
    private String game;
    private Instant startDate;
    private Instant endDate;
    private Instant registrationOpenAt;
    private Instant registrationCloseAt;
    private String rules;
    private Integer maxParticipants;
    private TournamentStatus status;
    private int participants;
    private int availableSlots;
    private Instant createdAt;
}