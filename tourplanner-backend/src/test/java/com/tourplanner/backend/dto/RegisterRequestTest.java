package com.tourplanner.backend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    @Test
    void constructorWithArgs() {
        RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password123");

        assertEquals("testuser", request.getUsername());
        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void defaultConstructor() {
        RegisterRequest request = new RegisterRequest();
        assertNotNull(request);
    }

    @Test
    void gettersAndSettersWork() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("newpassword");

        assertEquals("newuser", request.getUsername());
        assertEquals("new@example.com", request.getEmail());
        assertEquals("newpassword", request.getPassword());
    }
}
