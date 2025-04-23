package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.dto.TableStatusEnum;
import com.tablehub.thbackend.model.Table;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TableRepository extends JpaRepository<Table, Long> {

    int countTablesByRestaurantId(Long restaurantId);

    int countTablesByRestaurantIdAndStatus(Long restaurantId, TableStatusEnum status);

    Optional<Table> findByRestaurantIdAndSectionIdAndId (Long restaurantId, Long sectionId, Long id); // pls come up with better name than this shit
}
