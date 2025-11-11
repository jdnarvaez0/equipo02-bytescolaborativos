package com.codebytes2.recommender.auth.commons.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {

    private String accessToken;
}
