package com.codebytes2.recommender.product.controller;

import com.codebytes2.recommender.auth.service.JwtService;
import com.codebytes2.recommender.auth.service.impl.UserDetailsServiceImpl;
import com.codebytes2.recommender.config.security.SecurityConfig;
import com.codebytes2.recommender.controller.ProductController;
import com.codebytes2.recommender.dto.request.ProductCreateRequest;
import com.codebytes2.recommender.dto.request.ProductUpdateRequest;
import com.codebytes2.recommender.dto.response.ProductDetailDto;
import com.codebytes2.recommender.dto.response.ProductSummaryDto;
import com.codebytes2.recommender.service.ProductService;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private ProductCreateRequest validCreateRequest;
    private ProductUpdateRequest validUpdateRequest;
    private ProductDetailDto productDetailDto;
    private ProductSummaryDto productSummaryDto;
    private ProductDetailDto updatedProductDetailDto; // New DTO for updated product


    @BeforeEach
    void setUp() {
        validCreateRequest = new ProductCreateRequest();
        validCreateRequest.setName("Test Product");
        validCreateRequest.setDescription("Test Description");
        validCreateRequest.setCategory("Test Category");
        validCreateRequest.setTags(Set.of("tag1", "tag2"));
        validCreateRequest.setPopularityScore(100L);

        validUpdateRequest = new ProductUpdateRequest();
        validUpdateRequest.setName("Updated Product");
        validUpdateRequest.setDescription("Updated Description");
        validUpdateRequest.setCategory("Updated Category");
        validUpdateRequest.setTags(Set.of("tag3", "tag4"));
        validUpdateRequest.setPopularityScore(200L);

        UUID productId = UUID.randomUUID();
        productDetailDto = new ProductDetailDto();
        productDetailDto.setId(productId);
        productDetailDto.setName("Test Product");
        productDetailDto.setDescription("Test Description");
        productDetailDto.setCategory("Test Category");
        productDetailDto.setTags(Set.of("tag1", "tag2"));
        productDetailDto.setPopularityScore(100L);
        productDetailDto.setCreatedAt(Instant.now());

        productSummaryDto = new ProductSummaryDto();
        productSummaryDto.setId(productId);
        productSummaryDto.setName("Test Product");
        productSummaryDto.setCategory("Test Category");
        productSummaryDto.setPopularityScore(100L);

        // Initialize updatedProductDetailDto
        updatedProductDetailDto = new ProductDetailDto();
        updatedProductDetailDto.setId(productId);
        updatedProductDetailDto.setName(validUpdateRequest.getName());
        updatedProductDetailDto.setDescription(validUpdateRequest.getDescription());
        updatedProductDetailDto.setCategory(validUpdateRequest.getCategory());
        updatedProductDetailDto.setTags(validUpdateRequest.getTags());
        updatedProductDetailDto.setPopularityScore(validUpdateRequest.getPopularityScore());
        updatedProductDetailDto.setCreatedAt(Instant.now());
    }

    @Test
    @WithUserEntity(roles = {"ADMIN"})
    void createProduct_AsAdmin_ReturnsCreated() throws Exception {
        given(productService.createProduct(any(ProductCreateRequest.class))).willReturn(productDetailDto);

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(productDetailDto.getId().toString()))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    @WithUserEntity(roles = {"PLAYER"})
    void createProduct_AsPlayer_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void createProduct_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getProductDetail_Exists_ReturnsOk() throws Exception {
        UUID id = productDetailDto.getId();
        given(productService.getProductById(id)).willReturn(productDetailDto);

        mockMvc.perform(get("/api/products/{id}", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @WithMockUser
    void getAllProducts_ReturnsPageOfProducts() throws Exception {
        Page<ProductSummaryDto> page = new PageImpl<>(List.of(productSummaryDto));
        given(productService.getAllProducts(any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(productSummaryDto.getId().toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser
    void searchProducts_ReturnsPageOfProducts() throws Exception {
        Page<ProductSummaryDto> page = new PageImpl<>(List.of(productSummaryDto));
        given(productService.searchProducts(eq("Test"), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/products/search")
                        .param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Product"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithUserEntity(roles = {"ADMIN"})
    void updateProduct_AsAdmin_ReturnsOk() throws Exception {
        UUID productId = productDetailDto.getId(); // Use existing product ID
        given(productService.updateProduct(eq(productId), any(ProductUpdateRequest.class))).willReturn(updatedProductDetailDto);

        mockMvc.perform(put("/api/products/{id}", productId.toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedProductDetailDto.getId().toString()))
                .andExpect(jsonPath("$.name").value(validUpdateRequest.getName()))
                .andExpect(jsonPath("$.description").value(validUpdateRequest.getDescription()));
    }

    @Test
    @WithUserEntity(roles = {"PLAYER"})
    void updateProduct_AsPlayer_ReturnsForbidden() throws Exception {
        UUID productId = UUID.randomUUID();
        mockMvc.perform(put("/api/products/{id}", productId.toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserEntity(roles = {"ADMIN"})
    void deleteProduct_AsAdmin_ReturnsNoContent() throws Exception {
        UUID productId = UUID.randomUUID();
        doNothing().when(productService).deleteProduct(productId);

        mockMvc.perform(delete("/api/products/{id}", productId.toString())
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserEntity(roles = {"PLAYER"})
    void deleteProduct_AsPlayer_ReturnsForbidden() throws Exception {
        UUID productId = UUID.randomUUID();
        mockMvc.perform(delete("/api/products/{id}", productId.toString())
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserEntity(roles = {"ADMIN"})
    void createProduct_InvalidData_ReturnsBadRequest() throws Exception {
        ProductCreateRequest invalidRequest = new ProductCreateRequest();
        invalidRequest.setName(null); // Set name to null to trigger @NotBlank or @NotNull validation
        invalidRequest.setDescription("Some description");

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserEntity(roles = {"ADMIN"})
    void updateProduct_InvalidData_ReturnsBadRequest() throws Exception {
        ProductUpdateRequest invalidRequest = new ProductUpdateRequest();
        invalidRequest.setName(null); // Set name to null to trigger @NotBlank or @NotNull validation

        UUID productId = UUID.randomUUID();
        mockMvc.perform(put("/api/products/{id}", productId.toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}