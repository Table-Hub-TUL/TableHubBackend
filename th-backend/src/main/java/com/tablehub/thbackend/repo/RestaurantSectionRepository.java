package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.model.RestaurantSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RestaurantSectionRepository extends JpaRepository<RestaurantSection, Long> {

    @Query("SELECT s FROM RestaurantSection s " +
            "LEFT JOIN FETCH s.tables " +
            "WHERE s.id = :id")
    Optional<RestaurantSection> findByIdWithTables(@Param("id") Long id);
}
