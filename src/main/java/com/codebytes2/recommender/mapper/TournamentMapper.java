package com.codebytes2.recommender.mapper;

import com.codebytes2.recommender.dto.request.TournamentCreateRequest;
import com.codebytes2.recommender.dto.response.TournamentDetailDto;
import com.codebytes2.recommender.dto.response.TournamentSummaryDto;
import com.codebytes2.recommender.model.Tournament;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TournamentMapper {

    Tournament toEntity(TournamentCreateRequest request);

    TournamentDetailDto toDetailDto(Tournament tournament);

    TournamentSummaryDto toSummaryDto(Tournament tournament);
}
