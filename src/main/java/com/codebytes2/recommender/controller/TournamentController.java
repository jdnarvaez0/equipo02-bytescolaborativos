package com.codebytes2.recommender.controller;

import com.codebytes2.recommender.backend.TournamentStatus;
import com.codebytes2.recommender.dto.request.TournamentCreateRequest;
import com.codebytes2.recommender.dto.response.TournamentDetailDto;
import com.codebytes2.recommender.dto.response.TournamentSummaryDto;
import com.codebytes2.recommender.service.TournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService service;

    /**
     * Crea un nuevo torneo.
     * Requiere el rol 'ADMIN'.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TournamentDetailDto> createTournament(@Valid @RequestBody TournamentCreateRequest request) {
        TournamentDetailDto createdTournament = service.createTournament(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTournament);
    }

    /**
     * Obtiene los detalles de un torneo por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TournamentDetailDto> getTournamentDetail(@PathVariable UUID id) {
        TournamentDetailDto tournament = service.getTournamentById(id);
        return ResponseEntity.ok(tournament);
    }

    /**
     * Lista torneos filtrados por estado.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<TournamentSummaryDto>> listByStatus(
            @PathVariable TournamentStatus status,
            @PageableDefault(size = 20, sort = "startDate") Pageable pageable) {
        Page<TournamentSummaryDto> tournaments = service.getTournamentsByStatus(status, pageable);
        return ResponseEntity.ok(tournaments);
    }

    /**
     * Busca torneos por el nombre del juego.
     */
    @GetMapping("/game")
    public ResponseEntity<Page<TournamentSummaryDto>> searchByGame(
            @RequestParam String game,
            @PageableDefault(size = 20, sort = "startDate") Pageable pageable) {
        Page<TournamentSummaryDto> tournaments = service.searchTournamentsByGame(game, pageable);
        return ResponseEntity.ok(tournaments);
    }

    /**
     * Elimina un torneo por su ID.
     * Requiere el rol 'ADMIN'.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTournament(@PathVariable UUID id) {
        service.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }
}
