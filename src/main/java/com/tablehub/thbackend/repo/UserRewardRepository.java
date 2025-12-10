package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.model.UserReward;
import com.tablehub.thbackend.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRewardRepository extends JpaRepository<UserReward, Long> {
    List<UserReward> findAllByUser(AppUser user);
    Optional<UserReward> findByUserUserNameAndId(String username, Long id);
}