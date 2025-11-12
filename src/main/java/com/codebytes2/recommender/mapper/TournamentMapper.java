package com.codebytes2.recommender.mapper;

import com.codebytes2.recommender.dto.request.TournamentCreateRequest;
import com.codebytes2.recommender.dto.response.TournamentSummaryDto;
import com.codebytes2.recommender.dto.response.TournamentDetailDto;
import com.codebytes2.recommender.model.Tournament;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TournamentMapper {

    Tournament toEntity(TournamentCreateRequest request);

    @Mapping(target = "participants", expression = "java(tournament.getRegisteredCount())")
    TournamentSummaryDto toSummaryDto(Tournament tournament);

    @Mapping(target = "participants", expression = "java(tournament.getRegisteredCount())")
    @Mapping(target = "availableSlots", expression = "java(tournament.getAvailableSlots())")
    TournamentDetailDto toDetailDto(Tournament tournament);
}
