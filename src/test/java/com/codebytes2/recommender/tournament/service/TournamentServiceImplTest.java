package com.codebytes2.recommender.tournament.service;

import com.codebytes2.recommender.backend.TournamentStatus;
import com.codebytes2.recommender.dto.request.TournamentCreateRequest;
import com.codebytes2.recommender.dto.response.TournamentDetailDto;
import com.codebytes2.recommender.dto.response.TournamentSummaryDto;
import com.codebytes2.recommender.mapper.TournamentMapper;
import com.codebytes2.recommender.model.Tournament;
import com.codebytes2.recommender.repository.TournamentRepository;
import com.codebytes2.recommender.service.impl.TournamentServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceImplTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private TournamentMapper tournamentMapper;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    private Tournament tournament;
    private TournamentCreateRequest createRequest;
    private TournamentDetailDto detailDto;

    @BeforeEach
    void setUp() {
        UUID tournamentId = UUID.randomUUID();
        Instant now = Instant.now();

        tournament = Tournament.builder()
                .id(tournamentId)
                .name("Test Tournament")
                .game("Test Game")
                .status(TournamentStatus.OPEN)
                .startDate(now.plus(2, ChronoUnit.DAYS))
                .build();
        
        createRequest = new TournamentCreateRequest();
        createRequest.setName("Test Tournament");
        createRequest.setRegistrationOpenAt(now.plus(1, ChronoUnit.HOURS));
        createRequest.setRegistrationCloseAt(now.plus(1, ChronoUnit.DAYS));
        createRequest.setStartDate(now.plus(2, ChronoUnit.DAYS));
        createRequest.setEndDate(now.plus(3, ChronoUnit.DAYS));

        detailDto = new TournamentDetailDto();
        detailDto.setId(tournamentId);
        detailDto.setName("Test Tournament");
    }

    @Test
    void createTournament_Success() {
        when(tournamentMapper.toEntity(any(TournamentCreateRequest.class))).thenReturn(tournament);
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
        when(tournamentMapper.toDetailDto(any(Tournament.class))).thenReturn(detailDto);

        TournamentDetailDto result = tournamentService.createTournament(createRequest);

        assertNotNull(result);
        assertEquals(tournament.getId(), result.getId());
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    void createTournament_InvalidDates_ThrowsException() {
        createRequest.setStartDate(Instant.now());
        createRequest.setEndDate(Instant.now().minus(1, ChronoUnit.DAYS)); // End date before start date
        
        assertThrows(IllegalArgumentException.class, () -> {
            tournamentService.createTournament(createRequest);
        });
    }

    @Test
    void getTournamentById_Found() {
        when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));
        when(tournamentMapper.toDetailDto(tournament)).thenReturn(detailDto);

        TournamentDetailDto result = tournamentService.getTournamentById(tournament.getId());

        assertNotNull(result);
        assertEquals(tournament.getId(), result.getId());
    }

    @Test
    void getTournamentById_NotFound_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(tournamentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            tournamentService.getTournamentById(id);
        });
    }

    @Test
    void getTournamentsByStatus_ReturnsPage() {
        Pageable pageable = Pageable.unpaged();
        Page<Tournament> page = new PageImpl<>(List.of(tournament));
        when(tournamentRepository.findByStatus(TournamentStatus.OPEN, pageable)).thenReturn(page);
        
        TournamentSummaryDto summaryDto = new TournamentSummaryDto();
        when(tournamentMapper.toSummaryDto(any(Tournament.class))).thenReturn(summaryDto);

        Page<TournamentSummaryDto> result = tournamentService.getTournamentsByStatus(TournamentStatus.OPEN, pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        verify(tournamentRepository, times(1)).findByStatus(TournamentStatus.OPEN, pageable);
    }
    
    @Test
    void searchTournamentsByGame_ReturnsPage() {
        Pageable pageable = Pageable.unpaged();
        String game = "Test Game";
        Page<Tournament> page = new PageImpl<>(List.of(tournament));
        when(tournamentRepository.findByGameContainingIgnoreCase(game, pageable)).thenReturn(page);

        TournamentSummaryDto summaryDto = new TournamentSummaryDto();
        when(tournamentMapper.toSummaryDto(any(Tournament.class))).thenReturn(summaryDto);

        Page<TournamentSummaryDto> result = tournamentService.searchTournamentsByGame(game, pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        verify(tournamentRepository, times(1)).findByGameContainingIgnoreCase(game, pageable);
    }

    @Test
    void deleteTournament_Success() {
        when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));
        doNothing().when(tournamentRepository).deleteById(tournament.getId());

        assertDoesNotThrow(() -> {
            tournamentService.deleteTournament(tournament.getId());
        });

        verify(tournamentRepository, times(1)).deleteById(tournament.getId());
    }

    @Test
    void deleteTournament_AlreadyStarted_ThrowsException() {
        tournament.setStartDate(Instant.now().minus(1, ChronoUnit.DAYS)); // Tournament has started
        when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));
        
        assertThrows(IllegalStateException.class, () -> {
            tournamentService.deleteTournament(tournament.getId());
        });

        verify(tournamentRepository, never()).deleteById(any());
    }
}