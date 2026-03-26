package com.sagarmalasi.project.controllers;

import com.sagarmalasi.project.domain.dtos.*;
import com.sagarmalasi.project.domain.entities.User;
import com.sagarmalasi.project.mappers.UserMapper;
import com.sagarmalasi.project.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthController {

    private final AuthService authService;


    //registering user(by default every user is member)
    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> register(@RequestBody RegisterRequest request) {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    //Login using email and passworrd
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }








}
