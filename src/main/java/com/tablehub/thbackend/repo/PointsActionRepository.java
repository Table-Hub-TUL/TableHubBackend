package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.model.PointsAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointsActionRepository extends JpaRepository<PointsAction, Long> {
}