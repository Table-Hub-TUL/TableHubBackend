package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query("SELECT DISTINCT r FROM Restaurant r LEFT JOIN FETCH r.sections")
    List<Restaurant> findAllWithSections();
}
