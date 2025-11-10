package com.codebytes2.recommender.repository;

import com.codebytes2.recommender.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
