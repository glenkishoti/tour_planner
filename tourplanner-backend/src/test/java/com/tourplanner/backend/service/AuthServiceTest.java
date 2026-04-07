package com.tourplanner.backend.service;

import com.tourplanner.backend.dto.LoginRequest;
import com.tourplanner.backend.dto.LoginResponse;
import com.tourplanner.backend.dto.MessageResponse;
import com.tourplanner.backend.dto.RegisterRequest;
import com.tourplanner.backend.entity.User;
import com.tourplanner.backend.repository.UserRepository;
import com.tourplanner.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@test.com");
        testUser.setPassword("encodedPassword");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@test.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    void register_Success() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        MessageResponse result = authService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals("User registered successfully", result.getMessage());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_UsernameAlreadyExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginResponse_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken("testuser")).thenReturn("testJwtToken");

        // Act
        LoginResponse result = authService.loginResponse(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals("testJwtToken", result.getToken());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    void loginResponse_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> authService.loginResponse(loginRequest));
    }

    @Test
    void loginResponse_WrongPassword_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> authService.loginResponse(loginRequest));
    }

    @Test
    void loginResponse_WithEmail_Success() {
        // Arrange
        loginRequest.setUsernameOrEmail("test@test.com");
        when(userRepository.findByUsername("test@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken("testuser")).thenReturn("testJwtToken");

        // Act
        LoginResponse result = authService.loginResponse(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals("testJwtToken", result.getToken());
        assertEquals("testuser", result.getUsername());
    }
}
