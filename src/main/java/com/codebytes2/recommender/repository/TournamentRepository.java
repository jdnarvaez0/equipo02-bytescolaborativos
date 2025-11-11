package com.codebytes2.recommender.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.codebytes2.recommender.backend.TournamentStatus;
import com.codebytes2.recommender.model.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TournamentRepository extends JpaRepository<Tournament, UUID> {
    Page<Tournament> findByStatus(TournamentStatus status, Pageable pageable);
    Page<Tournament> findByGameIgnoreCaseContaining(String game, Pageable pageable);

    boolean existsById(Long id);

    void deleteById(Long id);

    Optional<Tournament> findById(Long id);


    Remapper findAll(Specification<Tournament> spec, java.awt.print.Pageable pageable);
}
