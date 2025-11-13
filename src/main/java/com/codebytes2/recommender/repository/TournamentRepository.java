package com.codebytes2.recommender.repository;

import com.codebytes2.recommender.backend.TournamentStatus;
import com.codebytes2.recommender.model.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface TournamentRepository extends JpaRepository<Tournament, UUID>, JpaSpecificationExecutor<Tournament> {
    Page<Tournament> findByStatus(TournamentStatus status, Pageable pageable);
    Page<Tournament> findByGameIgnoreCaseContaining(String game, Pageable pageable);

    boolean existsById(UUID id);

    void deleteById(UUID id);

    Optional<Tournament> findById(UUID id);
}
