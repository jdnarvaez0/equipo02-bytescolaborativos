package com.codebytes2.recommender.rating.controller;

import com.codebytes2.recommender.controller.RatingController;
import com.codebytes2.recommender.dto.request.ProductRatingRequest;
import com.codebytes2.recommender.dto.response.RatingResponseDto;
import com.codebytes2.recommender.service.RatingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(RatingController.class)
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RatingService ratingService;

    @Test
    @WithMockUser(roles = "PLAYER")
    void createRating_ValidRequest_ReturnsCreated() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();
        Integer score = 4;
        
        ProductRatingRequest request = ProductRatingRequest.builder()
                .productId(productId)
                .score(score)
                .build();
        
        RatingResponseDto responseDto = RatingResponseDto.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .productId(productId)
                .score(score)
                .build();
        
        given(ratingService.createRatingByUser(any(UUID.class),eq(productId), eq(score)))
                .willReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())) // Add CSRF token if needed
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(productId.toString()))
                .andExpect(jsonPath("$.score").value(score));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRating_WithAdminRole_ReturnsCreated() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();
        Integer score = 5;

        ProductRatingRequest request = ProductRatingRequest.builder()
                .productId(productId)
                .score(score)
                .build();

        RatingResponseDto responseDto = RatingResponseDto.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID()) // Random ID since we don't know the actual user ID
                .productId(productId)
                .score(score)
                .build();

        given(ratingService.createRatingByUser(any(UUID.class), eq(productId), eq(score)))
                .willReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score").value(score));
    }

    @Test
    void createRating_Unauthenticated_ReturnsUnauthorized() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();
        ProductRatingRequest request = ProductRatingRequest.builder()
                .productId(productId)
                .score(4)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "PLAYER")
    void getAverageRating_ProductHasRatings_ReturnsAverage() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();
        Double averageRating = 4.2;
        long totalRatings = 5;
        
        given(ratingService.getAverageRatingByProduct(eq(productId))).willReturn(averageRating);
        given(ratingService.getRatingCountByProduct(eq(productId))).willReturn(totalRatings);

        // Act & Assert
        mockMvc.perform(get("/api/ratings/average/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(averageRating))
                .andExpect(jsonPath("$.totalRatings").value(totalRatings));
    }

    @Test
    @WithMockUser(roles = "PLAYER")
    void getAverageRating_ProductNoRatings_ReturnsZeroAverage() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();
        
        given(ratingService.getAverageRatingByProduct(eq(productId))).willReturn(null);
        given(ratingService.getRatingCountByProduct(eq(productId))).willReturn(0L);

        // Act & Assert
        mockMvc.perform(get("/api/ratings/average/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(0.0))
                .andExpect(jsonPath("$.totalRatings").value(0));
    }
}