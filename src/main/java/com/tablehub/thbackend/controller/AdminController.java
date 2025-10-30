package com.tablehub.thbackend.controller;

import com.tablehub.thbackend.dto.request.AssignRoleRequest;
import com.tablehub.thbackend.dto.response.AuthMessageResponse;
import com.tablehub.thbackend.service.interfaces.AdminService;
import com.tablehub.thbackend.service.interfaces.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;
    private final GeocodingService geocodingService;


    @PostMapping("/users/{username}/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignRole(
            @PathVariable String username,
            @RequestBody AssignRoleRequest request) {
        log.info("Assigning role for user {} to {}", username, request.getRoleName());
        adminService.assignRole(username, request.getRoleName());
        return ResponseEntity.ok(new AuthMessageResponse("Role " + request.getRoleName() + " has been assigned to user " + username));
    }

    // test endpoint for geocoding to be deleted later in the development stages
    @GetMapping("/test-geocode")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> testGeocode(@RequestParam String address) {

        Point point = geocodingService.getCoordinatesFromAddress(address);

        if (point != null) {
            String coordinates = "Coordinates: " + point.getY() + ", " + point.getX();
            return ResponseEntity.ok(coordinates);
        } else {
            return ResponseEntity.badRequest().body("Could not geocode address from service.");
        }
    }
}
