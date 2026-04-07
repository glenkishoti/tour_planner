package com.tourplanner.backend.repository;

import com.tourplanner.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void findByUsername_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPassword("password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        Optional<User> found = userRepository.findByUsername("testuser");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        assertEquals(1L, found.get().getId());
    }

    @Test
    void findByUsername_NotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<User> found = userRepository.findByUsername("nonexistent");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void findByEmail_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("email@test.com");
        user.setPassword("password");

        when(userRepository.findByEmail("email@test.com")).thenReturn(Optional.of(user));

        // Act
        Optional<User> found = userRepository.findByEmail("email@test.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("email@test.com", found.get().getEmail());
    }

    @Test
    void existsByUsername_ReturnsTrue() {
        // Arrange
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act
        boolean exists = userRepository.existsByUsername("existinguser");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByUsername_ReturnsFalse() {
        // Arrange
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        // Act
        boolean exists = userRepository.existsByUsername("nonexistent");

        // Assert
        assertFalse(exists);
    }

    @Test
    void existsByEmail_ReturnsTrue() {
        // Arrange
        when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);

        // Act
        boolean exists = userRepository.existsByEmail("exists@example.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    void save_Success() {
        // Arrange
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("new@test.com");
        user.setPassword("password");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setEmail("new@test.com");
        savedUser.setPassword("password");

        when(userRepository.save(user)).thenReturn(savedUser);

        // Act
        User result = userRepository.save(user);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("newuser", result.getUsername());
    }
}
