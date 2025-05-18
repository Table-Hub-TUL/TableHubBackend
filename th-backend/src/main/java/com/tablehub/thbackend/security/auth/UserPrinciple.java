package com.tablehub.thbackend.security.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.tablehub.thbackend.model.AppUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserPrinciple implements UserDetails{
    private Long id;
    private String username;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserPrinciple(Long id, @NotBlank @Size(min = 3, max = 50) String username, @NotBlank @Size(min = 6, max = 100) String password, List<GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrinciple build(AppUser user) {
        List<GrantedAuthority> authorities = user.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getName().name())
        ).collect(Collectors.toList());

        return new UserPrinciple(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true     ;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o ==null || getClass() != o.getClass()) return false;
        UserPrinciple user = (UserPrinciple) o;
        return Objects.equals(id, user.id);
    }
}
