package com.tablehub.thbackend.service.implementations;

import com.tablehub.thbackend.dto.response.UserStatsDto;
import com.tablehub.thbackend.model.AppUser;
import com.tablehub.thbackend.model.Reward;
import com.tablehub.thbackend.model.UserReward;
import com.tablehub.thbackend.repo.RewardRepository;
import com.tablehub.thbackend.repo.UserRepository;
import com.tablehub.thbackend.repo.UserRewardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RewardRepository rewardRepository;
    @Mock private UserRewardRepository userRewardRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void redeemReward_SufficientPoints_DeductsCurrentPointsOnly() {
        String username = "testUser";
        AppUser user = AppUser.builder().userName(username).points(100).lifetimePoints(500).build();
        Reward reward = Reward.builder().id(1L).cost(60).title("Coffee").build();

        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));

        userService.redeemReward(username, 1L);

        assertEquals(40, user.getPoints(), "Current points should decrease (100 - 60)");
        assertEquals(500, user.getLifetimePoints(), "Lifetime points should remain unchanged");

        verify(userRepository).save(user);
        verify(userRewardRepository).save(any(UserReward.class));
    }

    @Test
    void getUserStats_UsesLifetimePointsForRanking() {
        String username = "rankUser";
        int lifetimePoints = 1000;
        AppUser user = AppUser.builder().userName(username).points(50).lifetimePoints(lifetimePoints).build();

        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));
        when(userRepository.countByLifetimePointsGreaterThan(lifetimePoints)).thenReturn(5L);

        UserStatsDto stats = userService.getUserStats(username);

        assertEquals(6, stats.getRanking(), "Ranking should be 5 + 1");
        verify(userRepository).countByLifetimePointsGreaterThan(lifetimePoints);
    }
}