package com.codebytes2.recommender.service.impl;

import com.codebytes2.recommender.backend.TournamentStatus;
import com.codebytes2.recommender.dto.request.TournamentCreateRequest;
import com.codebytes2.recommender.dto.response.TournamentDetailDto;
import com.codebytes2.recommender.dto.response.TournamentSummaryDto;
import com.codebytes2.recommender.service.TournamentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service; // <-- AÑADIDO

import java.util.UUID;

@Service // <-- AÑADIDO
public class TournamentServiceImpl implements TournamentService {
    @Override
    public TournamentDetailDto createTournament(TournamentCreateRequest request) {
        return null;
    }

    @Override
    public TournamentDetailDto getTournamentById(UUID id) {
        return null;
    }

    @Override
    public Page<TournamentSummaryDto> getTournamentsByStatus(TournamentStatus status, Pageable pageable) {
        return null;
    }

    @Override
    public Page<TournamentSummaryDto> searchTournamentsByGame(String game, Pageable pageable) {
        return null;
    }

    @Override
    public void deleteTournament(UUID id) {
    }
}
