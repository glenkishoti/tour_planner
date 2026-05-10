package com.tourplanner.backend.controller;

import com.tourplanner.backend.dto.LoginRequest;
import com.tourplanner.backend.dto.LoginResponse;
import com.tourplanner.backend.dto.MessageResponse;
import com.tourplanner.backend.dto.RegisterRequest;
import com.tourplanner.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.loginResponse(request));
    }
}
