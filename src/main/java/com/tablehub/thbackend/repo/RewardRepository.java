package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.model.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardRepository extends JpaRepository<Reward, Long> {
}
