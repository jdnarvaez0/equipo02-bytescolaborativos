package com.codebytes2.recommender.mapper;

import com.codebytes2.recommender.dto.request.TournamentCreateRequest;
import com.codebytes2.recommender.dto.response.TournamentDetailDto;
import com.codebytes2.recommender.dto.response.TournamentSummaryDto;
import com.codebytes2.recommender.model.Tournament;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TournamentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "registrations", ignore = true)
    Tournament toEntity(TournamentCreateRequest request);

    @Mapping(source = "registeredCount", target = "participants")
    TournamentDetailDto toDetailDto(Tournament tournament);

    @Mapping(source = "registeredCount", target = "participants")
    TournamentSummaryDto toSummaryDto(Tournament tournament);
}
