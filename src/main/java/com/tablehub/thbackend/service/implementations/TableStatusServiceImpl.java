package com.tablehub.thbackend.service.implementations;

import com.tablehub.thbackend.config.RabbitConfig;
import com.tablehub.thbackend.dto.internal.TableUpdateJob;
import com.tablehub.thbackend.dto.request.TableUpdateRequest;
import com.tablehub.thbackend.exception.InvalidTableDataException;
import com.tablehub.thbackend.exception.TableNotFoundException;
import com.tablehub.thbackend.model.*;
import com.tablehub.thbackend.repo.ActionRepository;
import com.tablehub.thbackend.repo.PointsActionRepository;
import com.tablehub.thbackend.repo.RestaurantTableRepository;
import com.tablehub.thbackend.repo.UserRepository;
import com.tablehub.thbackend.service.interfaces.TableStatusService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class TableStatusServiceImpl implements TableStatusService {

    private static final Logger logger = LoggerFactory.getLogger(TableStatusServiceImpl.class);

    private final RestaurantTableRepository tableRepository;
    private final RabbitTemplate rabbitTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    private final ActionRepository actionRepository;
    private final PointsActionRepository pointsActionRepository;
    private final UserRepository userRepository;
    private static final int MAX_CONFIDENCE = 100;
    private static final int CONFIDENCE_INCREMENT = 10;


    @Override
    @Transactional
    public void updateTableStatus(TableUpdateRequest request) {
        logger.info("Service: Updating table status for table ID: {}", request.getTableId());

        RestaurantTable table = tableRepository.findByIdWithSectionAndRestaurant(request.getTableId())
                .orElseThrow(() -> new TableNotFoundException(request.getTableId()));

        if (!table.getRestaurantSection().getId().equals(request.getSectionId()) ||
                !table.getRestaurantSection().getRestaurant().getId().equals(request.getRestaurantId())) {
            logger.error("Mismatch in section or restaurant ID for table ID: {}", request.getTableId());
            throw new InvalidTableDataException("Invalid section or restaurant");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        boolean isStatusChange = table.getStatus() != request.getRequestedStatus();

        ActionType actionType;
        OffsetDateTime now = OffsetDateTime.now();

        if (isStatusChange) {
            actionType = ActionType.REPORT_NEW;
            table.setStatus(request.getRequestedStatus());
            table.setConfidenceScore(MAX_CONFIDENCE);
            table.setLastUpdated(now);
            tableRepository.save(table);
        } else {
            actionType = ActionType.VALIDATE;

            tableRepository.incrementConfidenceScore(table.getId(), CONFIDENCE_INCREMENT, MAX_CONFIDENCE, now);

            table.setConfidenceScore(Math.min(MAX_CONFIDENCE, table.getConfidenceScore() + CONFIDENCE_INCREMENT));
            table.setLastUpdated(now);
        }

        logger.info("Successfully saved status for table ID: {} to {}", request.getTableId(), request.getRequestedStatus());

        awardPoints(user, actionType);

        TableUpdateRequest notificationPayload = new TableUpdateRequest();
        notificationPayload.setRestaurantId(request.getRestaurantId());
        notificationPayload.setSectionId(request.getSectionId());
        notificationPayload.setTableId(request.getTableId());
        notificationPayload.setRequestedStatus(request.getRequestedStatus());

        String individualTopic = "/topic/table-updates/" + request.getRestaurantId();
        messagingTemplate.convertAndSend(individualTopic, notificationPayload);
        logger.info("Broadcasted individual update for table ID: {} using {} payload to topic {}",
                request.getTableId(), notificationPayload.getClass().getSimpleName(), individualTopic);

        Long restaurantId = table.getRestaurantSection().getRestaurant().getId();
        TableUpdateJob job = new TableUpdateJob(restaurantId);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, job);
        logger.info("Dispatched aggregate update job for restaurant ID: {}", restaurantId);
    }

    private void awardPoints(AppUser user, ActionType actionType) {
        Action action;
        try {
            action = actionRepository.findByName(actionType)
                    .orElseGet(() -> actionRepository.save(Action.builder()
                            .name(actionType)
                            .points((short) actionType.getDefaultPoints())
                            .build()));
        } catch (DataIntegrityViolationException e) {
            logger.warn("Concurrent Action creation detected for type: {}. Retrying fetch.", actionType);
            action = actionRepository.findByName(actionType)
                    .orElseThrow(() -> new IllegalStateException("Action could not be found or created", e));
        }

        int pointsEarned = action.getPoints();

        userRepository.incrementPoints(user.getId(), pointsEarned);

        user.setPoints(user.getPoints() + pointsEarned);
        user.setLifetimePoints(user.getLifetimePoints() + pointsEarned);

        PointsAction history = PointsAction.builder()
                .user(user)
                .action(action)
                .timestamp(OffsetDateTime.now())
                .build();
        pointsActionRepository.save(history);

        logger.info("Awarded {} points to user {}. Total: {}, Lifetime: {}",
                pointsEarned, user.getUserName(), user.getPoints(), user.getLifetimePoints());
    }
}