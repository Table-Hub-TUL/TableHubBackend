package com.tablehub.thbackend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Column(columnDefinition = "geometry(Point,4326)", nullable = true)
    private Point location;

    @Column(nullable = false)
    private Double rating = 0.0;

    @OneToMany(mappedBy = "restaurant")
    @JsonManagedReference("restaurant-sections")
    private List<RestaurantSection> sections;
}
