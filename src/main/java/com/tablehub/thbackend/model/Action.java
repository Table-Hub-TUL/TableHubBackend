package com.tablehub.thbackend.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "action")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private ActionType name;

    @Column(nullable = false)
    private short points;

    @OneToMany(mappedBy = "action")
    private List<PointsAction> pointsActions;
}
