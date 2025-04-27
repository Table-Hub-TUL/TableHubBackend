package com.tablehub.thbackend.model;

import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

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

    @Column(unique = true, nullable = false)
    private String email;

    private String name;

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
