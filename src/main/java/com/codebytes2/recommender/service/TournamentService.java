package com.codebytes2.recommender.service;

import com.codebytes2.recommender.backend.TournamentStatus;
import com.codebytes2.recommender.dto.TournamentCreateRequest;
import com.codebytes2.recommender.dto.TournamentDetailDto;
import com.codebytes2.recommender.dto.TournamentSummaryDto;
import com.codebytes2.recommender.mapper.TournamentMapper;
import com.codebytes2.recommender.model.Tournament;
import com.codebytes2.recommender.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.print.Pageable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository repository;
    private final TournamentMapper mapper;

    public TournamentDetailDto createTournament(TournamentCreateRequest request) {
        if (request.startDate().isBefore(request.registrationCloseAt()) ||
                request.registrationOpenAt().isAfter(request.startDate()) ||
                request.startDate().isAfter(request.endDate())) {
            throw new IllegalArgumentException("Fechas incoherentes");
        }

        Tournament tournament = mapper.toEntity(request);
        tournament.setCreatedAt(Instant.now());
        tournament.setStatus(TournamentStatus.OPEN);
        tournament.setParticipants(new ArrayList<>());

        return mapper.toDetailDto(repository.save(tournament));
    }

    public Page<TournamentSummaryDto> listTournaments(Pageable pageable, String status, String game, String q) {
        Specification<Tournament> spec = (root, query, cb) -> cb.conjunction();

        if (status != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), TournamentStatus.valueOf(status.toUpperCase())));
        }
        if (game != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("game"), game));
        }
        if (q != null) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("name")), "%" + q.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("rules")), "%" + q.toLowerCase() + "%")
                    ));
        }

        return repository.findAll(spec, pageable).map(mapper::toSummaryDto);
    }

    public TournamentDetailDto getTournament(UUID id) {
        Tournament tournament = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapper.toDetailDto(tournament);
    }

    public void deleteTournament(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }
}