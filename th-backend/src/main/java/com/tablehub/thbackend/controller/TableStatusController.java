package com.tablehub.thbackend.controller;

import com.tablehub.thbackend.dto.request.TableUpdateRequest;
import com.tablehub.thbackend.dto.response.UpdateTableStatusResponse;
import com.tablehub.thbackend.dto.websocket.TableUpdateEvent;
import com.tablehub.thbackend.model.RestaurantTable;
import com.tablehub.thbackend.repo.RestaurantTableRepository;
import com.tablehub.thbackend.service.interfaces.RestaurantService;
import com.tablehub.thbackend.websocket.KafkaProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/table")
@RequiredArgsConstructor
@Tag(name = "Table Status Update", description = "Api for managing tables statuses")
public class TableStatusController {
    private static final Logger logger = LoggerFactory.getLogger(TableStatusController.class);

    private final RestaurantService restaurantService;

    @PostMapping("/update-status")
    @Operation(
            summary = "Update table status",
            description = "Updates the status of a restaurant table and broadcasts the change via WebSocket"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Table status updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Table not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid section or restaurant",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    public ResponseEntity<?> updateTableStatus(@RequestBody TableUpdateEvent request) {
        logger.info("Received request to update table status for table ID: {}", request.getTableID());

        try {
            UpdateTableStatusResponse response = restaurantService.updateTableStatus(request);
            logger.info("Successfully updated status for table ID: {} to {}",
                    response.getTableId(), response.getResultingStatus());
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException ex) {
            return (ResponseEntity<?>) ResponseEntity.notFound();
        }
    }
}
