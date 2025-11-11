package com.codebytes2.recommender.model;

import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.backend.RegistrationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tournament_registrations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tournament_id", "user_id"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TournamentRegistration {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status = RegistrationStatus.REGISTERED;

    @Column(nullable = false, updatable = false)
    private Instant registeredAt = Instant.now();

    public boolean isActive() {
        return status == RegistrationStatus.REGISTERED ||
                status == RegistrationStatus.CONFIRMED;
    }

}
