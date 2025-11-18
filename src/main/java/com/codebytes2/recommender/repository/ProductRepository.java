package com.codebytes2.recommender.repository;

import com.codebytes2.recommender.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
