package com.example.serviceA.application.service;

import com.example.serviceA.domain.exception.BusinessException;
import com.example.serviceA.infrastructure.repository.UserRepository;
import com.example.serviceA.infrastructure.util.JwtUtil;
import org.springframework.stereotype.Service;
import com.example.serviceA.domain.model.User;
import com.example.serviceA.shared.dto.LoginRequest;
import com.example.serviceA.shared.dto.LoginResponse;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse authenticate(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BusinessException("Invalid credentials", "CODE_001"));

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new BusinessException("Invalid credentials", "CODE_001");
        }

        String token = jwtUtil.generateToken(user.getId());
        return new LoginResponse(token);
    }
}
