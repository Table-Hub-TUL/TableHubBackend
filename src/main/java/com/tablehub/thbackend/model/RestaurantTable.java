package com.tablehub.thbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Table;
import lombok.*;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "restaurant_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_section_id", nullable = false)
    @JsonBackReference("section-tables")
    private RestaurantSection restaurantSection;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "position_x")),
            @AttributeOverride(name = "y", column = @Column(name = "position_y"))
    })
    private Position position;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "confidence_score", nullable = false)
    @Builder.Default
    private int confidenceScore = 0;

    @Column(name = "last_updated")
    private OffsetDateTime lastUpdated;
}
