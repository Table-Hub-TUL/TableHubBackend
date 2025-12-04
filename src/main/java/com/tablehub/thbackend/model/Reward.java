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

    private String title;
    private String additionalDescription;

    private String imageUrl; // url string in the DB? TODO: ask Julcio for clarification

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}