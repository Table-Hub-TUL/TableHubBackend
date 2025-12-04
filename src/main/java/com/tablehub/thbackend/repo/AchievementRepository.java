package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
}
