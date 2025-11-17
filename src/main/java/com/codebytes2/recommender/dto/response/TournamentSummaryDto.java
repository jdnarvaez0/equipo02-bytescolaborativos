package com.codebytes2.recommender.dto.response;

import com.codebytes2.recommender.backend.TournamentStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class TournamentSummaryDto {
    private UUID id;
    private String name;
    private String game;
    private TournamentStatus status;
    private Integer participants;
}