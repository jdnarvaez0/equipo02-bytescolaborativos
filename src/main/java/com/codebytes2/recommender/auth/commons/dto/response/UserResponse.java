package com.codebytes2.recommender.auth.commons.dto.response;

import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.auth.commons.models.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Schema(description = "DTO for user data in responses")
public class UserResponse {

    @Schema(description = "Unique identifier of the user", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Username of the user", example = "testuser")
    private String username;

    @Schema(description = "Email address of the user", example = "user@example.com")
    private String email;

    @Schema(description = "Set of roles assigned to the user", example = "[\"PLAYER\", \"ADMIN\"]")
    private Set<UserRole> roles;

    public static UserResponse fromEntity (UserEntity userEntity){
        return UserResponse.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .roles(userEntity.getRoles())
                .build();
    }
}
