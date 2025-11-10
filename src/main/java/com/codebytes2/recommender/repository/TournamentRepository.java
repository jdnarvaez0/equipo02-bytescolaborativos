package com.codebytes2.recommender.repository;

import com.codebytes2.recommender.backend.TournamentStatus;
import com.codebytes2.recommender.model.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TournamentRepository extends JpaRepository<Tournament, UUID> {
    Page<Tournament> findByStatus(TournamentStatus status, Pageable pageable);
    Page<Tournament> findByGameIgnoreCaseContaining(String game, Pageable pageable);
}
