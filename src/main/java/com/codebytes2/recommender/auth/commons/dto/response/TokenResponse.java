package com.codebytes2.recommender.auth.commons.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO for the authentication token response")
public class TokenResponse {

    @Schema(description = "JWT access token for authentication",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjE2NDk4MDIyLCJleHAiOjE2MTY1ODQ0MjJ9.4qf3fV8n-5dJ3z5b9X8cKz7m8wL6a5nJz-1hG2fX4rY")
    private String accessToken;
}
