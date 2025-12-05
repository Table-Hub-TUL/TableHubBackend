package com.tablehub.thbackend.dto.response;

import com.tablehub.thbackend.model.AppUser;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserProfileResponse {
    private Long id;
    private String userName;
    private String email;
    private String name;
    private int points;
    private Set<String> roles;
    private OffsetDateTime registeredAt;

    public UserProfileResponse(AppUser user) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.name = user.getName();
        this.points = user.getPoints();
        this.registeredAt = user.getRegisteredAt();
        this.roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}