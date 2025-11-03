package com.tablehub.thbackend.controller;

import com.tablehub.thbackend.dto.request.TableUpdateRequest;
import com.tablehub.thbackend.service.interfaces.TableStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/table")
@Tag(name = "Table Status Update", description = "Api for managing tables statuses")
@RequiredArgsConstructor
public class TableStatusController {

    private final TableStatusService tableStatusService;

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
        tableStatusService.updateTableStatus(request);
        return ResponseEntity.ok("Table status updated");
    }
}