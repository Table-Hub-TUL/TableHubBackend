package com.tablehub.thbackend.service.implementations;

import com.tablehub.thbackend.dto.*;
import com.tablehub.thbackend.model.Restaurant;
import com.tablehub.thbackend.repo.RestaurantRepository;
import com.tablehub.thbackend.repo.TableRepository;
import com.tablehub.thbackend.service.interfaces.RestaurantService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepo;
    private final TableRepository tableRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepo, TableRepository tableRepo, SimpMessagingTemplate messagingTemplate) {
        this.restaurantRepo = restaurantRepo;
        this.tableRepo = tableRepo;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantStatusDto> getAllRestaurantStatuses() {
        return restaurantRepo.findAll().stream().map(this::buildStatusDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantStatusDto getStatusFor(Long restaurantId) {
        Restaurant restaurant = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Restaurant not found: " + restaurantId)); // maybe try catching it later??
        return buildStatusDto(restaurant);
    }

    @Override
    public UpdateTableStatusResponse updateTableStatus(UpdateTableStatusRequest request, String username) {
        var table = tableRepo.findByRestaurantIdAndSectionIdAndId(request.getRestaurantId(), request.getSectionId(), request.getTableId()).orElseThrow(() -> new EntityNotFoundException(
                "Table not found: restaurant=" + request.getRestaurantId() +
                        ", section=" + request.getSectionId() +
                        ", table=" + request.getTableId()));
        table.setStatus(request.getRequestedStatus());
        tableRepo.save(table);

        UpdateTableStatusResponse response = new UpdateTableStatusResponse(request.getRestaurantId(), request.getSectionId(), request.getTableId(), true /* hardcoded for now*/,
                request.getRequestedStatus(), "Table updated successfully", 10);

        TableStatusChangedEvent event = new TableStatusChangedEvent(request.getRestaurantId(), request.getSectionId(), request.getTableId(), request.getRequestedStatus(), Instant.now().toEpochMilli());
        messagingTemplate.convertAndSend("/topic/restaurants." + request.getRestaurantId(), event);

        return response;
    }

    // Helper method turing Restaurant entity into DTO (counting total tables and free tables)
    private RestaurantStatusDto buildStatusDto(Restaurant restaurant) {
        int total = tableRepo.countTablesByRestaurantId(restaurant.getRestaurantId());
        int free  = tableRepo.countTablesByRestaurantIdAndStatus(
                restaurant.getRestaurantId(),
                TableStatusEnum.FREE);
        return new RestaurantStatusDto(
                restaurant.getRestaurantId(),
                restaurant.getName(),
                free,
                total,
                Instant.now()
        );
    }
}
