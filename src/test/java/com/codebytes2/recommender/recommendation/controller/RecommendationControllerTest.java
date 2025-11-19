package com.codebytes2.recommender.recommendation.controller;

import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.auth.service.JwtService;
import com.codebytes2.recommender.controller.RecommendationController;
import com.codebytes2.recommender.dto.response.RecommendationResponseDto;
import com.codebytes2.recommender.dto.response.RecommendedProductDto;
import com.codebytes2.recommender.service.RecommendationService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecommendationService recommendationService;

   @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser(roles = {"PLAYER"})
    void getRecommendations_ValidUser_ReturnsOk() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        RecommendedProductDto productDto = RecommendedProductDto.builder()
                .id(productId)
                .name("Test Product")
                .description("Test Description")
                .category("Test Category")
                .tags(Set.of("tag1", "tag2"))
                .averageRating(4.5)
                .popularityScore(100L)
                .relevanceScore(0.8)
                .build();

        RecommendationResponseDto responseDto = RecommendationResponseDto.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .recommendedProducts(Arrays.asList(productDto))
                .computedAt(Instant.now())
                .algorithmVersion("v1.0")
                .build();

        given(recommendationService.getRecommendationsForUser(eq(userId)))
                .willReturn(responseDto);

        // When & Then
        mockMvc.perform(get("/api/recommendations/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.recommendedProducts[0].id").value(productId.toString()))
                .andExpect(jsonPath("$.recommendedProducts[0].name").value("Test Product"))
                .andExpect(jsonPath("$.recommendedProducts[0].averageRating").value(4.5));
    }

    @Test
    @WithMockUser(roles = {"PLAYER"})
    void getRecommendations_ServiceThrowsException_ReturnsError() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();

        given(recommendationService.getRecommendationsForUser(eq(userId)))
                .willThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/recommendations/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getRecommendations_Unauthenticated_ReturnsUnauthorized() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(get("/api/recommendations/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"PLAYER"})
    void getRecommendations_InvalidUserId_ReturnsNotFound() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();

        given(recommendationService.getRecommendationsForUser(eq(userId)))
                .willThrow(new EntityNotFoundException("Usuario no encontrado con ID: " + userId));

        // When & Then
        mockMvc.perform(get("/api/recommendations/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"PLAYER"})
    void getRecommendations_EmptyRecommendations_ReturnsOkWithEmptyList() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();

        RecommendationResponseDto responseDto = RecommendationResponseDto.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .recommendedProducts(new ArrayList<>()) // Empty list
                .computedAt(Instant.now())
                .algorithmVersion("v1.0")
                .build();

        given(recommendationService.getRecommendationsForUser(eq(userId)))
                .willReturn(responseDto);

        // When & Then
        mockMvc.perform(get("/api/recommendations/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.recommendedProducts").isArray())
                .andExpect(jsonPath("$.recommendedProducts").isEmpty());
    }
}