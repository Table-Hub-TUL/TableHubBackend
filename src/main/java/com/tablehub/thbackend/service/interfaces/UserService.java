package com.tablehub.thbackend.service.interfaces;

import com.tablehub.thbackend.dto.request.ChangePasswordRequest;
import com.tablehub.thbackend.dto.request.UpdateUserProfileRequest;
import com.tablehub.thbackend.dto.response.AchievementDto;
import com.tablehub.thbackend.dto.response.RewardDto;
import com.tablehub.thbackend.dto.response.UserProfileResponse;
import com.tablehub.thbackend.dto.response.UserStatsDto;

import java.util.List;

public interface UserService {
    // Existing methods
    void changePassword(String username, ChangePasswordRequest request);
    UserProfileResponse getUserProfile(String username);
    UserProfileResponse updateUserProfile(String username, UpdateUserProfileRequest request);

    // New methods for Android App matching
    List<AchievementDto> getAchievements();
    UserStatsDto getUserStats(String username);
    List<RewardDto> getUserRewards(String username);
    void redeemReward(String username, Long rewardId);
}