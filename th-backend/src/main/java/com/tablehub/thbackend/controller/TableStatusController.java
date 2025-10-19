package com.tablehub.thbackend.controller;

import com.tablehub.thbackend.dto.request.TableUpdateRequest;
import com.tablehub.thbackend.model.RestaurantTable;
import com.tablehub.thbackend.repo.RestaurantTableRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Table Status Update", description = "Api for managing tables statuses")
public class TableStatusController {
    private static final Logger logger = LoggerFactory.getLogger(TableStatusController.class);

    private final RestaurantTableRepository tableRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public TableStatusController(RestaurantTableRepository tableRepository,
                                 SimpMessagingTemplate messagingTemplate) {
        this.tableRepository = tableRepository;
        this.messagingTemplate = messagingTemplate;
    }

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
    public ResponseEntity<String> updateTableStatus(@RequestBody TableUpdateRequest request) {
        logger.info("Received request to update table status for table ID: {}", request.getTableId());
        Optional<RestaurantTable> optionalTable = tableRepository.findById(request.getTableId());

        if (optionalTable.isEmpty()) {
            logger.warn("Table with ID {} not found.", request.getTableId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Table not found");
        }

        RestaurantTable table = optionalTable.get();

        if (!table.getRestaurantSection().getId().equals(request.getSectionId()) ||
                !table.getRestaurantSection().getRestaurant().getId().equals(request.getRestaurantId())) {
            logger.error("Mismatch in section or restaurant ID for table ID: {}", request.getTableId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid section or restaurant");
        }

        table.setStatus(request.getRequestedStatus());
        tableRepository.save(table);
        logger.info("Successfully updated status for table ID: {} to {}", request.getTableId(), request.getRequestedStatus());

        messagingTemplate.convertAndSend("/topic/table-updates", request);
        logger.info("Broadcasted table update for table ID: {} via WebSocket.", request.getTableId());
        return ResponseEntity.ok("Table status updated");
    }
}
