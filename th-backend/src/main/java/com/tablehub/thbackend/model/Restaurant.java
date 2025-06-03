package com.tablehub.thbackend.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.locationtech.jts.geom.Point;
import java.util.List;

@Entity
@Table(name = "restaurant")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CuisineName cuisineName;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(columnDefinition = "geometry(Point,4326)", nullable = false)
    private Point location;

    @OneToMany(mappedBy = "restaurant")
    private List<RestaurantSection> sections;
}
