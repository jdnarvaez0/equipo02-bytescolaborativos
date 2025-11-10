package com.codebytes2.recommender.repository;

import com.codebytes2.recommender.backend.RegistrationStatus;
import com.codebytes2.recommender.model.TournamentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TournamentRegistrationRepository extends JpaRepository<TournamentRegistration, UUID> {

    boolean existsByTournamentIdAndUserId(UUID tournamentId, UUID userId);

    @Query("SELECT COUNT(tr) FROM TournamentRegistration tr " +
            "WHERE tr.tournament.id = :tournamentId AND tr.status IN " +
            "(com.codebytes2.recommender.backend.RegistrationStatus.REGISTERED, " +
            " com.codebytes2.recommender.backend.RegistrationStatus.CONFIRMED)")
    long countActiveRegistrations(@Param("tournamentId") UUID tournamentId);

    List<TournamentRegistration> findByTournamentIdAndStatusIn(
            UUID tournamentId,
            List<RegistrationStatus> statuses
    );

}
