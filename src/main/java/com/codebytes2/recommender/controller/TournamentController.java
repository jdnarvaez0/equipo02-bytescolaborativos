package com.codebytes2.recommender.controller;

import com.codebytes2.recommender.dto.TournamentCreateRequest;
import com.codebytes2.recommender.dto.TournamentDetailDto;
import com.codebytes2.recommender.dto.TournamentSummaryDto;
import com.codebytes2.recommender.service.TournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;

@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
public class TournamentController {
    private final TournamentService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TournamentDetailDto> create(@Valid @RequestBody TournamentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTournament(request));
    }

    @GetMapping
    public ResponseEntity<Page<TournamentSummaryDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String game,
            @RequestParam(required = false) String q
    ) {
        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.by(sort != null ? sort : "startDate").ascending());
        return ResponseEntity.ok(service.listTournaments(pageable, status, game, q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentDetailDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTournament(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }
}