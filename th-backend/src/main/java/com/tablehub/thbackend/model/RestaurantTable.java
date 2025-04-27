package com.tablehub.thbackend.model;

import jakarta.persistence.Table;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurant_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantTable {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_section_id", nullable = false)
    private RestaurantSection restaurantSection;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableStatus status;

    @Column(name = "position_x")
    private Float positionX;

    @Column(name = "position_y")
    private Float positionY;

    @Column(nullable = false)
    private Integer capacity;
}
