package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.model.RestaurantSection;
import com.tablehub.thbackend.model.RestaurantTable;
import com.tablehub.thbackend.model.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    @Query("SELECT rt FROM RestaurantTable rt " +
            "JOIN FETCH rt.restaurantSection rs " +
            "JOIN FETCH rs.restaurant " +
            "WHERE rt.id = :id")
    Optional<RestaurantTable> findByIdWithSectionAndRestaurant(@Param("id") Long id);

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

    List<RestaurantTable> findByRestaurantSection(RestaurantSection section);

    @Modifying
    @Query("UPDATE RestaurantTable t SET " +
            "t.confidenceScore = CASE WHEN (t.confidenceScore + :increment) > :maxConfidence THEN :maxConfidence ELSE (t.confidenceScore + :increment) END, " +
            "t.lastUpdated = :lastUpdated " +
            "WHERE t.id = :id")
    void incrementConfidenceScore(@Param("id") Long id,
                                  @Param("increment") int increment,
                                  @Param("maxConfidence") int maxConfidence,
                                  @Param("lastUpdated") OffsetDateTime lastUpdated);

    @Modifying
    @Query("UPDATE RestaurantTable t SET t.confidenceScore = t.confidenceScore - :decayAmount " +
            "WHERE t.status <> :unknownStatus AND t.confidenceScore > 0")
    void decrementConfidenceForActiveTables(@Param("decayAmount") int decayAmount,
                                            @Param("unknownStatus") TableStatus unknownStatus);

    @Modifying
    @Query("UPDATE RestaurantTable t SET t.status = :unknownStatus, t.confidenceScore = 0 " +
            "WHERE t.status <> :unknownStatus AND t.confidenceScore <= 0")
    void resetExpiredTables(@Param("unknownStatus") TableStatus unknownStatus);
}