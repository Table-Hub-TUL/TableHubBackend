package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.model.UserReward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRewardRepository extends JpaRepository<UserReward, Long> {
    List<UserReward> findByUserId(Long userId);
    Optional<UserReward> findByUserIdAndRewardId(Long userId, Long rewardId);
}
