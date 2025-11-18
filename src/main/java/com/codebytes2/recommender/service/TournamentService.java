package com.codebytes2.recommender.service;

import com.codebytes2.recommender.backend.TournamentStatus;
import com.codebytes2.recommender.dto.request.TournamentCreateRequest;
import com.codebytes2.recommender.dto.request.TournamentJoinRequest;
import com.codebytes2.recommender.dto.response.TournamentDetailDto;
import com.codebytes2.recommender.dto.response.TournamentJoinResponse;
import com.codebytes2.recommender.dto.response.TournamentSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // <-- CORREGIDO
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface TournamentService {

        TournamentDetailDto createTournament(TournamentCreateRequest request);

        TournamentDetailDto getTournamentById(UUID id);

        Page<TournamentSummaryDto> getTournamentsByStatus(TournamentStatus status, Pageable pageable);

        Page<TournamentSummaryDto> searchTournamentsByGame(String game, Pageable pageable);

        Page<TournamentSummaryDto> getAllTournaments(Pageable pageable, TournamentStatus status, String game, String searchQuery);

        void deleteTournament(UUID id);

        TournamentJoinResponse joinTournament(UUID tournamentId, UUID userId, TournamentJoinRequest request);
    }
