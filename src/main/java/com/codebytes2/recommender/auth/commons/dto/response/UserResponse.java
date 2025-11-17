package com.codebytes2.recommender.auth.commons.dto.response;

import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.auth.commons.models.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class UserResponse {

    private UUID id;
    private String username;
    private String email;
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
