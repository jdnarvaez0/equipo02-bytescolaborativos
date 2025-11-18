package com.codebytes2.recommender.repository;

import com.codebytes2.recommender.backend.TournamentStatus;
import com.codebytes2.recommender.model.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TournamentRepository extends JpaRepository<Tournament, UUID> {
    Page<Tournament> findByStatus(TournamentStatus status, Pageable pageable);

    Page<Tournament> findByGameContainingIgnoreCase(String game, Pageable pageable);

    Page<Tournament> findByStatusAndGameContainingIgnoreCase(TournamentStatus status, String game, Pageable pageable);

    @Query("SELECT t FROM Tournament t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.game) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Tournament> findByNameContainingIgnoreCaseOrGameContainingIgnoreCase(@Param("query") String query, Pageable pageable);

    Page<Tournament> findAll(Pageable pageable);

    List<Tournament> findByStatus(TournamentStatus status);
}
