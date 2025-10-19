package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.model.Restaurant;
import com.tablehub.thbackend.model.RestaurantSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, JpaSpecificationExecutor<Restaurant> {

    @Query("SELECT DISTINCT r FROM Restaurant r " +
            "LEFT JOIN FETCH r.sections " +
            "LEFT JOIN FETCH r.address")
    List<Restaurant> findAllWithSections();

    @Query("SELECT DISTINCT s FROM RestaurantSection s " +
            "LEFT JOIN FETCH s.tables " +
            "WHERE s.restaurant.id IN :restaurantIds")
    List<RestaurantSection> findSectionsWithTablesByRestaurantIds(@Param("restaurantIds") List<Long> restaurantIds);

    @Query("SELECT r FROM Restaurant r " +
            "LEFT JOIN FETCH r.sections " +
            "LEFT JOIN FETCH r.address " +
            "WHERE r.id = :id")
    Optional<Restaurant> findByIdWithSections(@Param("id") Long id);

    @Query("SELECT DISTINCT s FROM RestaurantSection s " +
            "LEFT JOIN FETCH s.tables " +
            "WHERE s.restaurant.id = :restaurantId")
    List<RestaurantSection> findSectionsWithTablesByRestaurantId(@Param("restaurantId") Long restaurantId);
}