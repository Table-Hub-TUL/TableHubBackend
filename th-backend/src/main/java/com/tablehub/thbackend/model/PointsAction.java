package com.tablehub.thbackend.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "points_action")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointsAction {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "timestamp", nullable = false)
    private OffsetDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;
}
