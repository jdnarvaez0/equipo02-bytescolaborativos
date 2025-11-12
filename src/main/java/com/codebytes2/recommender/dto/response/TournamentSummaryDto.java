package com.codebytes2.recommender.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class TournamentSummaryDto {
    private UUID id;
    private String name;
    private String game;
    private int participants; // usa getRegisteredCount()
}