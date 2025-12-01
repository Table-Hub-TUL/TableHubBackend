package com.tablehub.thbackend.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class AuthJwtResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthJwtResponse(String token, String refreshToken, String username, Collection<? extends GrantedAuthority> authorities) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.username = username;
        this.authorities = authorities;
    }
}
