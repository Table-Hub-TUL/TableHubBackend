package com.tablehub.thbackend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.locationtech.jts.geom.Point;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "restaurant")
@Getter
@Setter
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
    @Builder.Default
    private Double rating = 0.0;

    @OneToMany(mappedBy = "restaurant")
    @JsonManagedReference("restaurant-sections")
    private List<RestaurantSection> sections;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private AppUser owner;

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Restaurant other)) return false;
        return Objects.equals(id, other.id);
    }
}