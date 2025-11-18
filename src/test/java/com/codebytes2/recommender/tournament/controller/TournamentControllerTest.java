package com.codebytes2.recommender.tournament.controller;

import com.codebytes2.recommender.auth.service.JwtService;
import com.codebytes2.recommender.auth.service.impl.UserDetailsServiceImpl;
import com.codebytes2.recommender.backend.TournamentStatus;
import com.codebytes2.recommender.config.security.SecurityConfig;
import com.codebytes2.recommender.controller.TournamentController;
import com.codebytes2.recommender.dto.request.TournamentCreateRequest;
import com.codebytes2.recommender.dto.response.TournamentDetailDto;
import com.codebytes2.recommender.dto.response.TournamentSummaryDto;
import com.codebytes2.recommender.service.TournamentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TournamentController.class)
@Import(SecurityConfig.class)
class TournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TournamentService tournamentService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private TournamentCreateRequest validCreateRequest;
    private TournamentDetailDto tournamentDetailDto;
    private TournamentSummaryDto tournamentSummaryDto;

    // Define the annotation required by the factory
    @WithSecurityContext(factory = WithUserEntitySecurityContextFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface WithUserEntity {
        String username() default "testuser";
        String[] roles() default {"PLAYER"};
    }

    @BeforeEach
    void setUp() {
        validCreateRequest = new TournamentCreateRequest();
        validCreateRequest.setName("Test Tournament");
        validCreateRequest.setGame("Test Game");
        validCreateRequest.setStartDate(Instant.now().plusSeconds(3600));
        validCreateRequest.setEndDate(Instant.now().plusSeconds(7200));
        validCreateRequest.setRegistrationOpenAt(Instant.now().plusSeconds(1800));
        validCreateRequest.setRegistrationCloseAt(Instant.now().plusSeconds(3000));
        validCreateRequest.setRules("Test Rules");
        validCreateRequest.setMaxParticipants(16);

        UUID tournamentId = UUID.randomUUID();
        tournamentDetailDto = new TournamentDetailDto();
        tournamentDetailDto.setId(tournamentId);
        tournamentDetailDto.setName("Test Tournament");
        tournamentDetailDto.setGame("Test Game");

        tournamentSummaryDto = new TournamentSummaryDto();
        tournamentSummaryDto.setId(tournamentId);
        tournamentSummaryDto.setName("Test Tournament");
        tournamentSummaryDto.setGame("Test Game");
    }

    @Test
    @WithUserEntity(roles = {"ADMIN"})
    void createTournament_AsAdmin_ReturnsCreated() throws Exception {
        given(tournamentService.createTournament(any(TournamentCreateRequest.class))).willReturn(tournamentDetailDto);

        mockMvc.perform(post("/api/tournaments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(tournamentDetailDto.getId().toString()))
                .andExpect(jsonPath("$.name").value("Test Tournament"));
    }

    @Test
    @WithUserEntity(roles = {"PLAYER"})
    void createTournament_AsPlayer_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/tournaments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createTournament_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getTournamentDetail_Exists_ReturnsOk() throws Exception {
        UUID id = tournamentDetailDto.getId();
        given(tournamentService.getTournamentById(id)).willReturn(tournamentDetailDto);

        mockMvc.perform(get("/api/tournaments/{id}", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Test Tournament"));
    }

    @Test
    void listByStatus_ReturnsPageOfTournaments() throws Exception {
        Page<TournamentSummaryDto> page = new PageImpl<>(List.of(tournamentSummaryDto));
        given(tournamentService.getTournamentsByStatus(eq(TournamentStatus.UPCOMING), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/tournaments/status/{status}", TournamentStatus.UPCOMING)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(tournamentSummaryDto.getId().toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void searchByGame_ReturnsPageOfTournaments() throws Exception {
        Page<TournamentSummaryDto> page = new PageImpl<>(List.of(tournamentSummaryDto));
        given(tournamentService.searchTournamentsByGame(eq("Test Game"), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/tournaments/game")
                        .param("game", "Test Game"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].game").value("Test Game"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }


    @Test
    @WithUserEntity(roles = {"ADMIN"})
    void deleteTournament_AsAdmin_ReturnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(tournamentService).deleteTournament(id);

        mockMvc.perform(delete("/api/tournaments/{id}", id.toString())
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserEntity(roles = {"PLAYER"})
    void deleteTournament_AsPlayer_ReturnsForbidden() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/api/tournaments/{id}", id.toString())
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}