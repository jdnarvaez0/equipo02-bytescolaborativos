package com.codebytes2.recommender.mapper;

import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.dto.request.ProductRatingRequest;
import com.codebytes2.recommender.dto.response.RatingResponseDto;
import com.codebytes2.recommender.model.Rating;
import com.codebytes2.recommender.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userEntity", ignore = true) // Will be set separately
    @Mapping(target = "product", ignore = true)   // Will be set separately
    @Mapping(target = "createdAt", ignore = true)  // Will be auto-generated
    Rating toEntity(ProductRatingRequest request);

    // Map from request with user and product
    default Rating toEntityFromRequestWithUserAndProduct(ProductRatingRequest request, UserEntity userEntity, Product product) {
        Rating rating = toEntity(request);
        rating.setUserEntity(userEntity);
        rating.setProduct(product);
        return rating;
    }

    RatingResponseDto toResponseDto(Rating rating);
}