package com.tablehub.thbackend.service.implementations;

import com.tablehub.thbackend.dto.response.RestaurantStatusDto;
import com.tablehub.thbackend.dto.request.UpdateTableStatusRequest;
import com.tablehub.thbackend.dto.response.UpdateTableStatusResponse;
import com.tablehub.thbackend.dto.event.TableStatusChangedEvent;
import com.tablehub.thbackend.model.Restaurant;
import com.tablehub.thbackend.model.RestaurantTable;
import com.tablehub.thbackend.model.TableStatus;
import com.tablehub.thbackend.repo.RestaurantRepository;
import com.tablehub.thbackend.repo.RestaurantTableRepository;
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
    private final RestaurantTableRepository tableRepo;
    private final SimpMessagingTemplate     messagingTemplate;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepo,
                                 RestaurantTableRepository tableRepo,
                                 SimpMessagingTemplate messagingTemplate) {
        this.restaurantRepo    = restaurantRepo;
        this.tableRepo         = tableRepo;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantStatusDto> getAllRestaurantStatuses() {
        return restaurantRepo.findAll()
                .stream()
                .map(this::buildStatusDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantStatusDto getStatusFor(Long restaurantId) {
        Restaurant restaurant = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found: " + restaurantId));
        return buildStatusDto(restaurant);
    }

    @Override
    public UpdateTableStatusResponse updateTableStatus(UpdateTableStatusRequest request,
                                                       String username) {
        RestaurantTable table = tableRepo
                .findByRestaurantSectionRestaurantIdAndRestaurantSectionIdAndId(
                        request.getRestaurantId(),
                        request.getSectionId(),
                        request.getTableId()
                )
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Table not found: restaurant=%d, section=%d, table=%d",
                                request.getRestaurantId(),
                                request.getSectionId(),
                                request.getTableId())
                ));

        table.setStatus(request.getRequestedStatus());
        tableRepo.save(table);

        UpdateTableStatusResponse response = new UpdateTableStatusResponse(
                request.getRestaurantId(),
                request.getSectionId(),
                request.getTableId(),
                true,
                request.getRequestedStatus(),
                "Table updated successfully",
                10
        );

        TableStatusChangedEvent event = new TableStatusChangedEvent(
                request.getRestaurantId(),
                request.getSectionId(),
                request.getTableId(),
                request.getRequestedStatus(),
                Instant.now().toEpochMilli()
        );
        messagingTemplate.convertAndSend(
                "/topic/restaurants/" + request.getRestaurantId(),
                event
        );

        return response;
    }

    // helper method
    private RestaurantStatusDto buildStatusDto(Restaurant restaurant) {
        long total = tableRepo.countByRestaurantSectionRestaurantId(restaurant.getId());
        long free  = tableRepo.countByRestaurantSectionRestaurantIdAndStatus(
                restaurant.getId(),
                TableStatus.AVAILABLE
        );

        return new RestaurantStatusDto(
                restaurant.getId(),
                restaurant.getName(),
                (int) free,
                (int) total,
                Instant.now()
        );
    }
}