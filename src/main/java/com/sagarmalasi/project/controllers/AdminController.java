package com.sagarmalasi.project.controllers;

import com.sagarmalasi.project.domain.Role;
import com.sagarmalasi.project.services.AdminService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/users/{userId}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable UUID userId,
            @RequestParam Role role
    ) {
        adminService.changeUserRole(userId, role);
        return ResponseEntity.noContent().build();
    }


}
