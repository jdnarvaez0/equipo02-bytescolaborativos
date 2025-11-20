package com.codebytes2.recommender.service.impl;

import com.codebytes2.recommender.dto.request.ProductCreateRequest;
import com.codebytes2.recommender.dto.request.ProductUpdateRequest;
import com.codebytes2.recommender.dto.response.ProductDetailDto;
import com.codebytes2.recommender.dto.response.ProductSummaryDto;
import com.codebytes2.recommender.exceptions.ProductHasRatingsException;
import com.codebytes2.recommender.mapper.ProductMapper;
import com.codebytes2.recommender.model.Product;
import com.codebytes2.recommender.repository.ProductRepository;
import com.codebytes2.recommender.repository.RatingRepository;
import com.codebytes2.recommender.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final RatingRepository ratingRepository;

    @Override
    public ProductDetailDto createProduct(ProductCreateRequest request) {
        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDetailDto(savedProduct);
    }

    @Override
    public ProductDetailDto getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));
        return productMapper.toDetailDto(product);
    }

    @Override
    public Page<ProductSummaryDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toSummaryDto);
    }

    @Override
    public ProductDetailDto updateProduct(UUID id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));

        productMapper.updateFromRequest(request, product);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDetailDto(updatedProduct);
    }

    @Override
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));

        // Check if product has ratings
        long ratingsCount = ratingRepository.countByProductId(id);
        if (ratingsCount > 0) {
            throw new ProductHasRatingsException(
                    "No se puede eliminar el producto porque tiene " + ratingsCount +
                            " valoración(es) asociada(s). Elimine primero las valoraciones.");
        }

        productRepository.delete(product);
    }

    @Override
    public Page<ProductSummaryDto> searchProducts(String name, Pageable pageable) {
        if (name == null || name.trim().isEmpty()) {
            return getAllProducts(pageable);
        }
        return productRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(productMapper::toSummaryDto);
    }
}