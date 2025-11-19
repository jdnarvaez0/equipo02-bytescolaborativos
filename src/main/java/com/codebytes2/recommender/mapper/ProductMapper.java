package com.codebytes2.recommender.mapper;

import com.codebytes2.recommender.dto.request.ProductCreateRequest;
import com.codebytes2.recommender.dto.request.ProductUpdateRequest;
import com.codebytes2.recommender.dto.response.ProductDetailDto;
import com.codebytes2.recommender.dto.response.ProductSummaryDto;
import com.codebytes2.recommender.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "popularityScore", source = "popularityScore", defaultExpression = "java(0L)")
    Product toEntity(ProductCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Product toEntityFromUpdateRequest(ProductUpdateRequest request);

    @Mapping(target = "tags", defaultExpression = "java(new java.util.HashSet<>())")
    Product toEntityFromCreateRequest(ProductCreateRequest request);

    ProductDetailDto toDetailDto(Product product);

    ProductSummaryDto toSummaryDto(Product product);

    List<ProductSummaryDto> toSummaryDtoList(List<Product> products);

    @Mapping(target = "id", ignore = true)
    void updateFromRequest(ProductUpdateRequest request, @MappingTarget Product product);
}