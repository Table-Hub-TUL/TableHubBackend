package com.tablehub.thbackend.controller;

import com.tablehub.thbackend.dto.request.UpdateUserProfileRequest;
import com.tablehub.thbackend.dto.response.AchievementDto;
import com.tablehub.thbackend.dto.response.RewardDto;
import com.tablehub.thbackend.dto.response.UserProfileResponse;
import com.tablehub.thbackend.dto.response.UserStatsDto;
import com.tablehub.thbackend.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Operations related to the current user's profile and stats")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get available achievements", description = "Retrieves a list of all system achievements")
    @GetMapping("/achievements")
    public ResponseEntity<List<AchievementDto>> getAchievements() {
        return ResponseEntity.ok(userService.getAchievements());
    }

    @Operation(summary = "Get user statistics", description = "Retrieves points, ranking, and report counts for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved stats", content = @Content(schema = @Schema(implementation = UserStatsDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @GetMapping("/{username}/stats")
    @PreAuthorize("#username == authentication.name")
    public ResponseEntity<UserStatsDto> getUserStats(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserStats(username));
    }

    @Operation(summary = "Get user rewards", description = "Retrieves list of rewards available or redeemed by the user")
    @GetMapping("/{username}/rewards")
    @PreAuthorize("#username == authentication.name")
    public ResponseEntity<List<RewardDto>> getUserRewards(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserRewards(username));
    }

    @Operation(summary = "Redeem a reward", description = "Processes a reward redemption for the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reward redeemed successfully"),
            @ApiResponse(responseCode = "400", description = "Insufficient points or invalid reward"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/{username}/rewards/{rewardId}")
    @PreAuthorize("#username == authentication.name")
    public ResponseEntity<Void> redeemReward(
            @PathVariable String username,
            @PathVariable Long rewardId) {
        userService.redeemReward(username, rewardId);
        return ResponseEntity.ok().build();
    }
}