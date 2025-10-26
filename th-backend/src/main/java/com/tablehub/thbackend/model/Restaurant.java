package com.tablehub.thbackend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    // TODO: Review maybe create separate table and add automatic region selection
    @Column(nullable = false)
    @Pattern(regexp = "^tables\\.[a-z0-9-]+(?:\\.[a-z0-9-]+){2}$")
    private String region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private AppUser owner;
}
