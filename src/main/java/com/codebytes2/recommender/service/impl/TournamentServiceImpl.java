package com.codebytes2.recommender.service.impl;

import com.codebytes2.recommender.backend.TournamentStatus;
import com.codebytes2.recommender.dto.request.TournamentCreateRequest;
import com.codebytes2.recommender.dto.response.TournamentDetailDto;
import com.codebytes2.recommender.dto.response.TournamentSummaryDto;
import com.codebytes2.recommender.mapper.TournamentMapper;
import com.codebytes2.recommender.model.Tournament;
import com.codebytes2.recommender.repository.TournamentRepository;
import com.codebytes2.recommender.service.TournamentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;

    @Override
    public TournamentDetailDto createTournament(TournamentCreateRequest request) {

        if (!request.getRegistrationOpenAt().isBefore(request.getRegistrationCloseAt())) {
            throw new IllegalArgumentException("La apertura de registro debe ser antes del cierre.");
        }
        if (!request.getRegistrationCloseAt().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("El cierre de registro debe ser antes del inicio.");
        }
        if (!request.getStartDate().isBefore(request.getEndDate())) {
            throw new IllegalArgumentException("La fecha de inicio debe ser antes de la finalización.");
        }

        Tournament tournament = tournamentMapper.toEntity(request);
        tournament.setStatus(TournamentStatus.OPEN);
        Tournament saved = tournamentRepository.save(tournament);
        return tournamentMapper.toDetailDto(saved);
    }

    @Override
    public TournamentDetailDto getTournamentById(UUID id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Torneo no encontrado con ID: " + id));
        return tournamentMapper.toDetailDto(tournament);
    }

    @Override
    public Page<TournamentSummaryDto> getTournamentsByStatus(TournamentStatus status, Pageable pageable) {
        return tournamentRepository.findByStatus(status, pageable)
                .map(tournamentMapper::toSummaryDto);
    }

    @Override
    public Page<TournamentSummaryDto> searchTournamentsByGame(String game, Pageable pageable) {
        return tournamentRepository.findByGameContainingIgnoreCase(game, pageable)
                .map(tournamentMapper::toSummaryDto);
    }

    @Override
    public void deleteTournament(UUID id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Torneo no encontrado"));

        if (tournament.getStartDate().isBefore(Instant.now())) {
            throw new IllegalStateException("No se puede eliminar un torneo que ya ha comenzado.");
        }

        tournamentRepository.deleteById(id);
    }
}