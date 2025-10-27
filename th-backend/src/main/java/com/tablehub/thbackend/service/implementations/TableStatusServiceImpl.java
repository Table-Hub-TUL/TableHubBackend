package com.tablehub.thbackend.service.implementations;

import com.tablehub.thbackend.config.RabbitConfig;
import com.tablehub.thbackend.dto.internal.TableUpdateJob;
import com.tablehub.thbackend.dto.request.TableUpdateRequest;
import com.tablehub.thbackend.exception.InvalidTableDataException;
import com.tablehub.thbackend.exception.TableNotFoundException;
import com.tablehub.thbackend.model.RestaurantTable;
import com.tablehub.thbackend.repo.RestaurantTableRepository;
import com.tablehub.thbackend.service.interfaces.TableStatusService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TableStatusServiceImpl implements TableStatusService {

    private static final Logger logger = LoggerFactory.getLogger(TableStatusServiceImpl.class);

    private final RestaurantTableRepository tableRepository;
    private final RabbitTemplate rabbitTemplate;

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

        table.setStatus(request.getRequestedStatus());
        tableRepository.save(table);
        logger.info("Successfully saved status for table ID: {} to {}", request.getTableId(), request.getRequestedStatus());

        Long restaurantId = table.getRestaurantSection().getRestaurant().getId();
        TableUpdateJob job = new TableUpdateJob(restaurantId);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, job);

        logger.info("Dispatched table update job for restaurant ID: {}", restaurantId);
    }
}