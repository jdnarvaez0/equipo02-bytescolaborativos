package com.codebytes2.recommender.product.service;

import com.codebytes2.recommender.dto.request.ProductCreateRequest;
import com.codebytes2.recommender.dto.request.ProductUpdateRequest;
import com.codebytes2.recommender.dto.response.ProductDetailDto;
import com.codebytes2.recommender.dto.response.ProductSummaryDto;
import com.codebytes2.recommender.mapper.ProductMapper;
import com.codebytes2.recommender.model.Product;
import com.codebytes2.recommender.repository.ProductRepository;
import com.codebytes2.recommender.service.impl.ProductServiceImpl;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductCreateRequest createRequest;
    private ProductUpdateRequest updateRequest;
    private ProductDetailDto detailDto;

    @BeforeEach
    void setUp() {
        UUID productId = UUID.randomUUID();

        product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setDescription("Test Description");

        createRequest = new ProductCreateRequest();
        createRequest.setName("Test Product");
        createRequest.setDescription("Test Description");

        updateRequest = new ProductUpdateRequest();
        updateRequest.setName("Updated Product");
        updateRequest.setDescription("Updated Description");

        detailDto = new ProductDetailDto();
        detailDto.setId(productId);
        detailDto.setName("Test Product");
        detailDto.setDescription("Test Description");
    }

    @Test
    void createProduct_Success() {
        when(productMapper.toEntity(any(ProductCreateRequest.class))).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDetailDto(any(Product.class))).thenReturn(detailDto);

        ProductDetailDto result = productService.createProduct(createRequest);

        assertNotNull(result);
        assertEquals(detailDto.getId(), result.getId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getProductById_Found() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productMapper.toDetailDto(product)).thenReturn(detailDto);

        ProductDetailDto result = productService.getProductById(product.getId());

        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
    }

    @Test
    void getProductById_NotFound_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            productService.getProductById(id);
        });
    }

    @Test
    void getAllProducts_ReturnsPage() {
        Pageable pageable = Pageable.unpaged();
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findAll(pageable)).thenReturn(page);

        ProductSummaryDto summaryDto = new ProductSummaryDto();
        when(productMapper.toSummaryDto(any(Product.class))).thenReturn(summaryDto);

        Page<ProductSummaryDto> result = productService.getAllProducts(pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void updateProduct_Success() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDetailDto(any(Product.class))).thenReturn(detailDto);

        ProductDetailDto result = productService.updateProduct(product.getId(), updateRequest);

        assertNotNull(result);
        verify(productMapper, times(1)).updateFromRequest(updateRequest, product);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateProduct_NotFound_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            productService.updateProduct(id, updateRequest);
        });
    }

    @Test
    void deleteProduct_Success() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        assertDoesNotThrow(() -> {
            productService.deleteProduct(product.getId());
        });

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void deleteProduct_NotFound_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            productService.deleteProduct(id);
        });
    }

    @Test
    void searchProducts_WithName_ReturnsMatchingProducts() {
        Pageable pageable = Pageable.unpaged();
        String name = "Test";
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findByNameContainingIgnoreCase(name, pageable)).thenReturn(page);

        ProductSummaryDto summaryDto = new ProductSummaryDto();
        when(productMapper.toSummaryDto(any(Product.class))).thenReturn(summaryDto);

        Page<ProductSummaryDto> result = productService.searchProducts(name, pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        verify(productRepository, times(1)).findByNameContainingIgnoreCase(name, pageable);
    }

    @Test
    void searchProducts_WithNullName_ReturnsAllProducts() {
        Pageable pageable = Pageable.unpaged();
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findAll(pageable)).thenReturn(page);

        ProductSummaryDto summaryDto = new ProductSummaryDto();
        when(productMapper.toSummaryDto(any(Product.class))).thenReturn(summaryDto);

        Page<ProductSummaryDto> result = productService.searchProducts(null, pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        verify(productRepository, times(1)).findAll(pageable);
    }
}