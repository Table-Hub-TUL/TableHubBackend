package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.model.PointsAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PointsActionRepository extends JpaRepository<PointsAction, Long> {

    /**
     * Counts how many times a specific user has performed a specific action.
     * Useful for stats like "Total Reports".
     */
    @Query("SELECT COUNT(pa) FROM PointsAction pa " +
            "WHERE pa.user.id = :userId AND pa.action.name = :actionName")
    long countByUserIdAndActionName(@Param("userId") Long userId,
                                    @Param("actionName") String actionName);
}