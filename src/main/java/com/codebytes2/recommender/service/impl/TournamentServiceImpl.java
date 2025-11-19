package com.codebytes2.recommender.service.impl;

import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.auth.repository.UserEntityRepository;
import com.codebytes2.recommender.backend.TournamentStatus;
import com.codebytes2.recommender.dto.request.TournamentCreateRequest;
import com.codebytes2.recommender.dto.request.TournamentJoinRequest;
import com.codebytes2.recommender.dto.response.TournamentDetailDto;
import com.codebytes2.recommender.dto.response.TournamentJoinResponse;
import com.codebytes2.recommender.dto.response.TournamentSummaryDto;
import com.codebytes2.recommender.mapper.TournamentMapper;
import com.codebytes2.recommender.model.Tournament;
import com.codebytes2.recommender.model.TournamentRegistration;
import com.codebytes2.recommender.repository.TournamentRegistrationRepository;
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
    private final UserEntityRepository userEntityRepository;
    private final TournamentRegistrationRepository tournamentRegistrationRepository;
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

    @Override
    public Page<TournamentSummaryDto> getAllTournaments(Pageable pageable, TournamentStatus status, String game, String searchQuery) {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            // Buscar por texto libre en nombre o descripción
            return tournamentRepository.findByNameContainingIgnoreCaseOrGameContainingIgnoreCase(
                    searchQuery, pageable)
                    .map(tournamentMapper::toSummaryDto);
        } else if (status != null && game != null) {
            // Filtrar por status y game
            return tournamentRepository.findByStatusAndGameContainingIgnoreCase(status, game, pageable)
                    .map(tournamentMapper::toSummaryDto);
        } else if (status != null) {
            // Filtrar solo por status
            return tournamentRepository.findByStatus(status, pageable)
                    .map(tournamentMapper::toSummaryDto);
        } else if (game != null) {
            // Filtrar solo por game
            return tournamentRepository.findByGameContainingIgnoreCase(game, pageable)
                    .map(tournamentMapper::toSummaryDto);
        } else {
            // Sin filtros, devolver todos
            return tournamentRepository.findAll(pageable)
                    .map(tournamentMapper::toSummaryDto);
        }
    }

    @Override
    public TournamentJoinResponse joinTournament(UUID tournamentId, UUID userId, TournamentJoinRequest request) {
        // Validar existencia del torneo
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("Torneo no encontrado con ID: " + tournamentId));

        // Validar existencia del usuario
        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + userId));

        // Comprobar que esté dentro del periodo de inscripción
        Instant now = Instant.now();
        if (now.isBefore(tournament.getRegistrationOpenAt()) || now.isAfter(tournament.getRegistrationCloseAt())) {
            throw new IllegalStateException("El periodo de inscripción no está activo. Apertura: " +
                    tournament.getRegistrationOpenAt() + ", Cierre: " + tournament.getRegistrationCloseAt());
        }

        // Revisar plazas disponibles
        if (tournament.getAvailableSlots() <= 0) {
            throw new IllegalStateException("No hay plazas disponibles en este torneo");
        }

        // Asegurar que el usuario no esté ya inscrito
        if (tournamentRegistrationRepository.existsByTournamentIdAndUserEntityId(tournamentId, userId)) {
            throw new IllegalStateException("El usuario ya está inscrito en este torneo");
        }

        // Crear la inscripción
        TournamentRegistration registration = TournamentRegistration.builder()
                .tournament(tournament)
                .userEntity(user)
                .nickname(request.getNickname())
                .build();

        tournamentRegistrationRepository.save(registration);

        // Actualizar status del torneo si es necesario
        if (tournament.getRegisteredCount() >= tournament.getMaxParticipants() &&
            tournament.getMaxParticipants() != null) {
            tournament.setStatus(TournamentStatus.CLOSED);
            tournamentRepository.save(tournament);
        }

        return TournamentJoinResponse.builder()
                .message("Inscripción completada")
                .tournamentId(tournamentId)
                .userId(userId)
                .status("REGISTERED")
                .build();
    }
}