package com.codebytes2.recommender.model;

import com.codebytes2.recommender.backend.TournamentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tournaments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tournament {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String game;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = false)
    private Instant endDate;

    @Column(nullable = false)
    private Instant registrationOpenAt;

    @Column(nullable = false)
    private Instant registrationCloseAt;

    private String rules;

    private Integer maxParticipants;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "tournament", fetch = FetchType.LAZY)
    private List<TournamentRegistration> registrations = new ArrayList<>();

    @Transient
    public int getRegisteredCount() {
        return registrations.stream()
                .filter(TournamentRegistration::isActive)
                .toList()
                .size();
    }

    @Transient
    public int getAvailableSlots() {
        if (maxParticipants == null) return Integer.MAX_VALUE;
        return Math.max(0, maxParticipants - getRegisteredCount());
    }

}
