package com.codebytes2.recommender.service;

import com.codebytes2.recommender.dto.request.ProductCreateRequest;
import com.codebytes2.recommender.dto.request.ProductUpdateRequest;
import com.codebytes2.recommender.dto.response.ProductDetailDto;
import com.codebytes2.recommender.dto.response.ProductSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface ProductService {
    
    ProductDetailDto createProduct(ProductCreateRequest request);

    ProductDetailDto getProductById(UUID id);

    Page<ProductSummaryDto> getAllProducts(Pageable pageable);

    ProductDetailDto updateProduct(UUID id, ProductUpdateRequest request);

    void deleteProduct(UUID id);
    
    Page<ProductSummaryDto> searchProducts(String name, Pageable pageable);
}