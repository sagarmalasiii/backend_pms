package com.sagarmalasi.project.controllers;

import com.sagarmalasi.project.domain.dtos.UserPerformanceDto;
import com.sagarmalasi.project.domain.entities.User;
import com.sagarmalasi.project.domain.entities.UserPerformance;
import com.sagarmalasi.project.mappers.UserPerformanceMapper;
import com.sagarmalasi.project.repositories.UserRepository;
import com.sagarmalasi.project.services.UserPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/performance")
@RequiredArgsConstructor
public class UserPerformanceController {
    private final UserPerformanceService performanceService;
    private final UserRepository userRepository;
    private final UserPerformanceMapper userPerformanceMapper;

    @GetMapping("/{userId}")
    public ResponseEntity<UserPerformanceDto> getPerformance(@PathVariable UUID userId) {
        User member = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserPerformance performance = performanceService.getByUser(member);
        return ResponseEntity.ok(userPerformanceMapper.toDto(performance));
    }
}
