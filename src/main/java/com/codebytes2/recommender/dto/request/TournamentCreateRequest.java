package com.codebytes2.recommender.dto.request;

import java.time.Instant;

import com.codebytes2.recommender.backend.TournamentStatus;
import lombok.Data;

@Data
public class TournamentCreateRequest {
    private String name;
    private String game;
    private Instant startDate;
    private Instant endDate;
    private Instant registrationOpenAt;
    private Instant registrationCloseAt;
    private String rules;
    private Integer maxParticipants;
    private TournamentStatus status;
}