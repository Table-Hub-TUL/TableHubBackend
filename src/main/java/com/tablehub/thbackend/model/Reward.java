package com.tablehub.thbackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reward")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String additionalDescription;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "url", column = @Column(name = "image_url")),
            @AttributeOverride(name = "altText", column = @Column(name = "image_alt_text")),
            @AttributeOverride(name = "ratio", column = @Column(name = "image_ratio"))
    })
    private Image image;

    @Column(nullable = false)
    private int cost;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}