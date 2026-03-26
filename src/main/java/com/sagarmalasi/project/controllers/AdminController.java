package com.sagarmalasi.project.controllers;

import com.sagarmalasi.project.domain.Role;
import com.sagarmalasi.project.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/admin")
public class AdminController {
    private final AdminService adminService;


//Only Admin can update user roles(mainly member to the manager and back)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable UUID userId,
            @RequestParam Role role
    ) {
        adminService.changeUserRole(userId, role);
        return ResponseEntity.noContent().build();
    }




}
