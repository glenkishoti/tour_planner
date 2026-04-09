package com.tourplanner.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourplanner.backend.dto.LoginRequest;
import com.tourplanner.backend.dto.LoginResponse;
import com.tourplanner.backend.dto.MessageResponse;
import com.tourplanner.backend.dto.RegisterRequest;
import com.tourplanner.backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerUser_success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@test.com");
        request.setPassword("password123");

        when(authService.register(any())).thenReturn(new MessageResponse("User registered successfully"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void loginUser_success() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsernameOrEmail("loginuser");
        login.setPassword("password123");

        when(authService.loginResponse(any())).thenReturn(
                new LoginResponse("testtoken", "loginuser", "login@test.com")
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("loginuser"));
    }

    @Test
    void loginUser_wrongPassword() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsernameOrEmail("wrongpass");
        login.setPassword("wrongpassword");

        when(authService.loginResponse(any())).thenThrow(
                new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid credentials"
                )
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }
}
