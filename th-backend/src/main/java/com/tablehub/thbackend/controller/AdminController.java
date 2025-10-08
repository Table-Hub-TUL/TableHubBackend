package com.tablehub.thbackend.controller;

import com.tablehub.thbackend.dto.request.AssignRoleRequest;
import com.tablehub.thbackend.dto.response.AuthMessageResponse;
import com.tablehub.thbackend.service.interfaces.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/users/{username}/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignRole(
            @PathVariable String username,
            @RequestBody AssignRoleRequest request) {

        adminService.assignRole(username, request.getRoleName());

        return ResponseEntity.ok(new AuthMessageResponse("Role " + request.getRoleName() + " has been assigned to user " + username));
    }
}
