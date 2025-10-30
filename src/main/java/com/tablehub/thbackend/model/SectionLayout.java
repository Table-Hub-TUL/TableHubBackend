package com.tablehub.thbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "section_layout")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionLayout implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int viewportWidth;

    @Column(nullable = false)
    private int viewportHeight;

    @Column(nullable = false)
    private String shape;
}
