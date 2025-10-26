package com.tablehub.thbackend.service.implementations;

import com.tablehub.thbackend.dto.RestaurantSubscriptionInitialState;
import com.tablehub.thbackend.dto.types.SectionDto;
import com.tablehub.thbackend.dto.request.UpdateTableStatusRequest;
import com.tablehub.thbackend.dto.response.RestaurantStatusDto;
import com.tablehub.thbackend.dto.response.RestaurantSubscriptionResponse;
import com.tablehub.thbackend.dto.response.UpdateTableStatusResponse;
import com.tablehub.thbackend.dto.websocket.TableUpdateEvent;
import com.tablehub.thbackend.model.Restaurant;
import com.tablehub.thbackend.model.RestaurantTable;
import com.tablehub.thbackend.model.TableStatus;
import com.tablehub.thbackend.repo.RestaurantRepository;
import com.tablehub.thbackend.repo.RestaurantTableRepository;
import com.tablehub.thbackend.service.interfaces.RestaurantService;
import com.tablehub.thbackend.websocket.KafkaProducer;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaProducer kafkaProducer;

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

    @Override
    @Transactional
    public UpdateTableStatusResponse updateTableStatus(TableUpdateEvent request) {
        RestaurantTable table = restaurantTableRepository.findByRestaurantSectionRestaurantIdAndId(
                request.getRestaurantID(), request.getTableID()
        ).orElseThrow(() -> new EntityNotFoundException("Cannot find requested table"));
        if (table.getStatus().equals(request.getTableStatus())) {
            return new UpdateTableStatusResponse(
                    request.getRestaurantID(),
                    request.getTableID(),
                    false,
                    request.getTableStatus(),
                    "This table already has requested status",
                    0
            );
        }
        table.setStatus(request.getTableStatus());
        restaurantTableRepository.save(table);
        kafkaProducer.sendMessage(String.format("%s", request.getRegion()), request);
        return new UpdateTableStatusResponse(
                request.getRestaurantID(),
                request.getTableID(),
                true,
                request.getTableStatus(),
                "Successfully updated table status",
                0
        );
    }

    @Transactional(readOnly = true)
    public void sendInitialSubscriptionState(String username) {
        List<Restaurant> restaurants = restaurantRepository.findAllWithSections();
        for (Restaurant restaurant : restaurants) {
            List<SectionDto> sectionDtos = restaurant.getSections().stream()
                    .map(section -> new SectionDto(section.getId(), section.getName(), null, null, null))
                    .collect(Collectors.toList());

            RestaurantSubscriptionInitialState initialState = new RestaurantSubscriptionInitialState(restaurant.getId(), sectionDtos);
            RestaurantSubscriptionResponse response = new RestaurantSubscriptionResponse(
                    restaurant.getId(),
                    true,
                    "Initial state",
                    initialState
            );
            messagingTemplate.convertAndSendToUser(username, "/queue/restaurant-status", response);
        }
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