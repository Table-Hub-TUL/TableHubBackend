package com.tablehub.thbackend.service.implementations;

import com.tablehub.thbackend.dto.request.ChangePasswordRequest;
import com.tablehub.thbackend.dto.request.UpdateUserProfileRequest;
import com.tablehub.thbackend.dto.response.AchievementDto;
import com.tablehub.thbackend.dto.response.RewardDto;
import com.tablehub.thbackend.dto.response.UserProfileResponse;
import com.tablehub.thbackend.dto.response.UserStatsDto;
import com.tablehub.thbackend.dto.types.AddressDto;
import com.tablehub.thbackend.model.AppUser;
import com.tablehub.thbackend.model.Reward;
import com.tablehub.thbackend.model.UserReward;
import com.tablehub.thbackend.repo.ActionRepository;
import com.tablehub.thbackend.repo.RewardRepository;
import com.tablehub.thbackend.repo.UserRepository;
import com.tablehub.thbackend.repo.UserRewardRepository;
import com.tablehub.thbackend.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ActionRepository actionRepository;
    private final RewardRepository rewardRepository;
    private final UserRewardRepository userRewardRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        AppUser user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String username) {
        AppUser user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new UserProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(String username, UpdateUserProfileRequest request) {
        AppUser user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }

        AppUser updatedUser = userRepository.save(user);
        return new UserProfileResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AchievementDto> getAchievements() {
        return actionRepository.findAll().stream()
                .map(AchievementDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatsDto getUserStats(String username) {
        AppUser user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        long ranking = userRepository.countByLifetimePointsGreaterThan(user.getLifetimePoints()) + 1;

        int reportsCount = user.getPointsActions() != null ? user.getPointsActions().size() : 0;

        return new UserStatsDto(user.getPoints(), reportsCount, (int) ranking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RewardDto> getUserRewards(String username) {
        AppUser user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return userRewardRepository.findAllByUser(user).stream()
                .map(ur -> new RewardDto(
                        ur.getId(),
                        ur.getReward().getTitle(),
                        ur.getReward().getAdditionalDescription(),
                        ur.getReward().getImage(),
                        ur.getReward().getRestaurant().getName(),
                        new AddressDto(
                                ur.getReward().getRestaurant().getAddress().getStreetNumber(),
                                ur.getReward().getRestaurant().getAddress().getStreet(),
                                ur.getReward().getRestaurant().getAddress().getApartmentNumber(),
                                ur.getReward().getRestaurant().getAddress().getCity(),
                                ur.getReward().getRestaurant().getAddress().getPostalCode(),
                                ur.getReward().getRestaurant().getAddress().getCountry()
                        ),
                        ur.isRedeemed()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void redeemReward(String username, Long rewardId) {
        AppUser user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Reward rewardDefinition = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new IllegalArgumentException("Reward definition not found"));

        if (user.getPoints() < rewardDefinition.getCost()) {
            throw new IllegalArgumentException("Insufficient points to redeem this reward.");
        }

        user.setPoints(user.getPoints() - rewardDefinition.getCost());
        userRepository.save(user);

        UserReward newUserReward = UserReward.builder()
                .user(user)
                .reward(rewardDefinition)
                .redeemed(false)
                .build();

        userRewardRepository.save(newUserReward);
    }
}