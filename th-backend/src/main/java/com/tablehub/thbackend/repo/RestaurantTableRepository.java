package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.model.RestaurantTable;
import com.tablehub.thbackend.model.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    Optional<RestaurantTable> findByRestaurantSectionRestaurantIdAndRestaurantSectionIdAndId(
            Long restaurantId,
            Long sectionId,
            Long tableId
    );

    long countByRestaurantSectionRestaurantId(Long restaurantId);

    long countByRestaurantSectionRestaurantIdAndStatus(
            Long restaurantId,
            TableStatus status
    );
}

