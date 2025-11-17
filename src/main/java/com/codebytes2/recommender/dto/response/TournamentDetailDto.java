package com.codebytes2.recommender.dto.response;

import com.codebytes2.recommender.backend.TournamentStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TournamentDetailDto {
    private UUID id;
    private String name;
    private String game;
    private String rules;
    private TournamentStatus status;
    private Instant startDate;
    private Instant endDate;
    private Instant registrationOpenAt;
    private Instant registrationCloseAt;
    private Integer maxParticipants;
    private Integer participants;
}