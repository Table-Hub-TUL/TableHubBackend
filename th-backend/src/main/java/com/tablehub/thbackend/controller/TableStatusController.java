package com.tablehub.thbackend.controller;

@RestController
@RequestMapping("/api/table")
public class TableStatusController {

    private final RestaurantTableRepository tableRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public TableStatusController(RestaurantTableRepository tableRepository,
                                 SimpMessagingTemplate messagingTemplate) {
        this.tableRepository = tableRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/update-status")
    public ResponseEntity<String> updateTableStatus(@RequestBody TableUpdateRequest request) {

        Optional<RestaurantTable> optionalTable = tableRepository.findById(request.getTableId());

        if (optionalTable.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Table not found");
        }

        RestaurantTable table = optionalTable.get();

        if (!table.getRestaurantSection().getId().equals(request.getSectionId()) ||
                !table.getRestaurantSection().getRestaurant().getId().equals(request.getRestaurantId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid section or restaurant");
        }

        table.setStatus(request.getRequestedStatus());
        tableRepository.save(table);

        messagingTemplate.convertAndSend("/topic/table-updates", request);

        return ResponseEntity.ok("Table status updated");
    }
}
