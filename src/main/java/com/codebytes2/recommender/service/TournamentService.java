package com.codebytes2.recommender.service;

import com.codebytes2.recommender.backend.TournamentStatus;
import com.codebytes2.recommender.dto.request.TournamentCreateRequest;
import com.codebytes2.recommender.dto.response.TournamentDetailDto;
import com.codebytes2.recommender.dto.response.TournamentSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.UUID;

@Service
public interface TournamentService {

        TournamentDetailDto createTournament(TournamentCreateRequest request);

        TournamentDetailDto getTournamentById(UUID id);

        Page<TournamentSummaryDto> getTournamentsByStatus(TournamentStatus status, Pageable pageable);

        Page<TournamentSummaryDto> searchTournamentsByGame(String game, Pageable pageable);

        void deleteTournament(UUID id);
    }
