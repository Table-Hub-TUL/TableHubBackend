package com.tablehub.thbackend.service.implementations;

import com.tablehub.thbackend.dto.response.RestaurantStatusDto;
import com.tablehub.thbackend.model.Restaurant;
import com.tablehub.thbackend.model.TableStatus;
import com.tablehub.thbackend.repo.RestaurantRepository;
import com.tablehub.thbackend.repo.RestaurantTableRepository;
import com.tablehub.thbackend.service.interfaces.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository restaurantTableRepository;


    @Override
    @Transactional(readOnly = true)
    public List<RestaurantStatusDto> getAllRestaurantStatuses() {
        return restaurantRepository.findAll().stream()
                .map(this::mapToRestaurantStatusDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantStatusDto getStatusFor(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .map(this::mapToRestaurantStatusDto)
                .orElse(null);
    }

    private RestaurantStatusDto mapToRestaurantStatusDto(Restaurant restaurant) {
        long totalTables = restaurantTableRepository.countByRestaurantSectionRestaurantId(restaurant.getId());
        long freeTables = restaurantTableRepository.countByRestaurantSectionRestaurantIdAndStatus(restaurant.getId(), TableStatus.AVAILABLE);
        return new RestaurantStatusDto(
                restaurant.getId(),
                restaurant.getName(),
                (int) freeTables,
                (int) totalTables,
                Instant.now()
        );
    }
}