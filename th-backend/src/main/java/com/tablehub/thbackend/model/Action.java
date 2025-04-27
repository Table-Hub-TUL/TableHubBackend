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
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private short points;

    @OneToMany(mappedBy = "action")
    private List<PointsAction> pointsActions;
}
