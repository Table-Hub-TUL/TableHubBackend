package com.tablehub.thbackend.model;

import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {
    @Id
    private Long id;

    @Column(name = "auth_ref", nullable = false)
    private String authRef;

    @NotBlank
    @Size(min = 3, max = 100)
    private String userName;

    @NotBlank
    @Size(min = 3, max = 100)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    @Column(name = "registered_at", nullable = false)
    private OffsetDateTime registeredAt;

    @Column(nullable = false)
    private int points = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @OneToMany(mappedBy = "user")
    private List<PointsAction> pointsActions;
}
