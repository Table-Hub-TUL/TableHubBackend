package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUserName(String userName);
    Boolean existsByUserName(String userName);

    long countByPointsGreaterThan(int points);
}