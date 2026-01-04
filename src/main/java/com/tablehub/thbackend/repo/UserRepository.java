package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.model.AppUser;
import io.jsonwebtoken.security.Jwks;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUserName(String userName);
    Boolean existsByUserName(String userName);
    long countByLifetimePointsGreaterThan(int lifetimePoints);

    @Modifying
    @Query("UPDATE AppUser u SET u.points = u.points + :amount, u.lifetimePoints = u.lifetimePoints + :amount WHERE u.id = :id")
    void incrementPoints(@Param("id") Long id, @Param("amount") int amount);

    @Modifying
    @Query("UPDATE AppUser u SET u.points = u.points - :cost WHERE u.id = :id AND u.points >= :cost")
    int deductPoints(@Param("id") Long id, @Param("cost") int cost);

    Optional<AppUser> findByEmail(@Email String email);
}