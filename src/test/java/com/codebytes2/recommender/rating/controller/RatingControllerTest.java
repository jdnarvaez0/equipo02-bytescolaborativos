package com.codebytes2.recommender.rating.controller;


import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.controller.RatingController;
import com.codebytes2.recommender.dto.request.ProductRatingRequest;
import com.codebytes2.recommender.dto.response.RatingResponseDto;
import com.codebytes2.recommender.auth.service.JwtService;
import com.codebytes2.recommender.service.RatingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.UUID;

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
    
    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithUserEntity(roles = {"PLAYER"})
    void createRating_ValidRequest_ReturnsCreated() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();
        Integer score = 4;

        // Get the authenticated user from the SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity authenticatedUser = (UserEntity) authentication.getPrincipal();
        UUID authenticatedUserId = authenticatedUser.getId();

        ProductRatingRequest request = ProductRatingRequest.builder()
                .productId(productId)
                .score(score)
                .build();

        RatingResponseDto responseDto = RatingResponseDto.builder()
                .id(UUID.randomUUID())
                .userId(authenticatedUserId) // Use the ID from the authenticated user
                .productId(productId)
                .score(score)
                .build();

        given(ratingService.createRatingByUser(eq(authenticatedUserId), eq(productId), eq(score)))
                .willReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(productId.toString()))
                .andExpect(jsonPath("$.score").value(score))
                .andExpect(jsonPath("$.userId").value(authenticatedUserId.toString()));
    }

    @Test
    @WithUserEntity(roles = {"ADMIN"})
    void createRating_WithAdminRole_ReturnsCreated() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();
        Integer score = 5;

        // Get the authenticated user from the SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity authenticatedUser = (UserEntity) authentication.getPrincipal();
        UUID authenticatedUserId = authenticatedUser.getId();

        ProductRatingRequest request = ProductRatingRequest.builder()
                .productId(productId)
                .score(score)
                .build();

        RatingResponseDto responseDto = RatingResponseDto.builder()
                .id(UUID.randomUUID())
                .userId(authenticatedUserId) // Use the ID from the authenticated user
                .productId(productId)
                .score(score)
                .build();

        given(ratingService.createRatingByUser(eq(authenticatedUserId), eq(productId), eq(score)))
                .willReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(productId.toString()))
                .andExpect(jsonPath("$.score").value(score))
                .andExpect(jsonPath("$.userId").value(authenticatedUserId.toString())); // Assert the userId
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
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())) // Añadir CSRF para que la petición llegue al filtro de autenticación
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserEntity(roles = {"PLAYER"})
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
    @WithUserEntity(roles = {"PLAYER"})
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
    
    @Test
    @WithUserEntity(roles = {"PLAYER"})
    void createRating_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();
        Integer score = 4;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity authenticatedUser = (UserEntity) authentication.getPrincipal();
        UUID authenticatedUserId = authenticatedUser.getId();

        ProductRatingRequest request = ProductRatingRequest.builder()
                .productId(productId)
                .score(score)
                .build();

        given(ratingService.createRatingByUser(eq(authenticatedUserId), eq(productId), eq(score)))
                .willThrow(new RuntimeException("Service failure"));

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithUserEntity(roles = {"PLAYER"})
    void createRating_NullProductId_ReturnsBadRequest() throws Exception {
        // Arrange
        ProductRatingRequest request = ProductRatingRequest.builder()
                .productId(null) // Invalid productId
                .score(4)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserEntity(roles = {"PLAYER"})
    void createRating_InvalidScore_ReturnsBadRequest() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();
        ProductRatingRequest request = ProductRatingRequest.builder()
                .productId(productId)
                .score(6) // Invalid score
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }
    
    @WithSecurityContext(factory = WithUserEntitySecurityContextFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface WithUserEntity {
        String username() default "testuser";
        String[] roles() default {"PLAYER"};
    }
}