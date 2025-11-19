package com.codebytes2.recommender.mapper;

import com.codebytes2.recommender.dto.response.RecommendationResponseDto;
import com.codebytes2.recommender.dto.response.RecommendedProductDto;
import com.codebytes2.recommender.model.Recommendation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    RecommendationMapper INSTANCE = Mappers.getMapper(RecommendationMapper.class);

    @Mapping(target = "recommendedProducts", ignore = true)
    @Mapping(target = "computedAt", source = "recommendation.computedAt")
    @Mapping(target = "algorithmVersion", source = "recommendation.algorithmVersion")
    RecommendationResponseDto toResponseDto(Recommendation recommendation);

    List<RecommendedProductDto> toRecommendedProductDtoList(List<com.codebytes2.recommender.model.Product> products);

    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "relevanceScore", ignore = true)
    RecommendedProductDto toRecommendedProductDto(com.codebytes2.recommender.model.Product product);
}