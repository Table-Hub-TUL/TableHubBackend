package com.tablehub.thbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "restaurant_section")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantSection {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SectionName name;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonBackReference("restaurant-sections")
    private Restaurant restaurant;

    @OneToMany(mappedBy = "restaurantSection")
    @JsonManagedReference("section-tables")
    private List<RestaurantTable> tables;
}