package com.sagarmalasi.project.services;

import com.sagarmalasi.project.domain.Role;
import com.sagarmalasi.project.domain.dtos.AuthResponse;
import com.sagarmalasi.project.domain.dtos.LoginRequest;
import com.sagarmalasi.project.domain.dtos.RegisterRequest;
import com.sagarmalasi.project.domain.dtos.UserDto;
import com.sagarmalasi.project.domain.entities.User;
import com.sagarmalasi.project.mappers.UserMapper;
import com.sagarmalasi.project.repositories.UserRepository;
import com.sagarmalasi.project.security.CustomUserDetails;
import com.sagarmalasi.project.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final UserMapper userMapper;

    public AuthResponse login(LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.generateToken(new CustomUserDetails(user));

        return new AuthResponse(token);
    }

    @Transactional
    public UserDto register(RegisterRequest request) {
        User userToRegister = userMapper.toEntity(request);
        if(userRepository.existsByEmail(request.getEmail())){
            throw  new IllegalArgumentException("Email already used."+request.getEmail());
        } else if (userRepository.existsByUsername(request.getUsername())) {
            throw  new IllegalArgumentException("Username already taken."+request.getUsername());
        }
        userToRegister.setRole(Role.MEMBER);
        userToRegister.setPassword(encoder.encode(request.getPassword()));
        userToRegister.setIsActive(true);
        User newUser = userRepository.save(userToRegister);
        return userMapper.toDto(newUser);

    }
}

