package com.tablehub.thbackend.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;

@Entity
@Table(name = "users") // Table name in the database
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // This should store a hashed password (BCrypt)

    // Add more fields if needed
    // e.g., email, roles, isActive, createdAt, etc.

    // --- Constructors ---
    public User() {} // Default constructor required by JPA

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
