package com.sagarmalasi.project.controllers;

import com.sagarmalasi.project.domain.dtos.AuthResponse;
import com.sagarmalasi.project.domain.dtos.LoginRequest;
import com.sagarmalasi.project.domain.dtos.RegisterRequest;
import com.sagarmalasi.project.domain.dtos.UserDto;
import com.sagarmalasi.project.domain.entities.User;
import com.sagarmalasi.project.mappers.UserMapper;
import com.sagarmalasi.project.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequest request) {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }




}
