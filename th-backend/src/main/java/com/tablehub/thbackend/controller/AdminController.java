package com.tablehub.thbackend.controller;

import com.tablehub.thbackend.dto.request.AssignRoleRequest;
import com.tablehub.thbackend.dto.response.AuthMessageResponse;
import com.tablehub.thbackend.service.interfaces.AdminService;
import com.tablehub.thbackend.service.interfaces.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.SQLOutput;

@RestController
@RequestMapping("/api/admin")
//@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final GeocodingService geocodingService;
    private final RestTemplate restTemplate;


    public AdminController(AdminService adminService, GeocodingService geocodingService, RestTemplateBuilder restTemplateBuilder) {
        this.adminService = adminService;
        this.geocodingService = geocodingService;
        this.restTemplate = restTemplateBuilder.build();
    }

    @PostMapping("/users/{username}/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignRole(
            @PathVariable String username,
            @RequestBody AssignRoleRequest request) {

        adminService.assignRole(username, request.getRoleName());

        return ResponseEntity.ok(new AuthMessageResponse("Role " + request.getRoleName() + " has been assigned to user " + username));
    }

    // test endpoint for geocoding to be deleted later in the development stages
    @GetMapping("/test-geocode")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> testGeocode(@RequestParam String address) {

        String url = "https://nominatim.openstreetmap.org/search";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("q", address)
                .queryParam("format", "json")
                .queryParam("limit", 1);

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");
        headers.set("Accept", "application/json, text/plain, */*");
        headers.set("Accept-Language", "en-US,en;q=0.9");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        System.out.println("Sending geocoding request to URL: " + builder.toUriString());

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response;

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during API call: " + e.getMessage());
        }
    }
}
