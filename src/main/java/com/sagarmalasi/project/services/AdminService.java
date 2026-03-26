package com.sagarmalasi.project.services;

import com.sagarmalasi.project.domain.Role;
import com.sagarmalasi.project.domain.dtos.UserDto;
import com.sagarmalasi.project.domain.entities.User;
import com.sagarmalasi.project.mappers.UserMapper;
import com.sagarmalasi.project.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public void changeUserRole(UUID userId, Role role) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setRole(role);
    }


}
