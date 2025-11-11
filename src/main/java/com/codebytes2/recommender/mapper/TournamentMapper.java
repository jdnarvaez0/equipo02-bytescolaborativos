package com.codebytes2.recommender.mapper;

import com.codebytes2.recommender.dto.TournamentCreateRequest;
import com.codebytes2.recommender.dto.TournamentDetailDto;
import com.codebytes2.recommender.dto.TournamentSummaryDto;
import com.codebytes2.recommender.model.Tournament;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TournamentMapper {
    Tournament toEntity(TournamentCreateRequest request);
    TournamentSummaryDto toSummaryDto(Tournament tournament);
    TournamentDetailDto toDetalDto(Tournament tournament);

    TournamentDetailDto toDetailDto(Tournament tournament);
}
