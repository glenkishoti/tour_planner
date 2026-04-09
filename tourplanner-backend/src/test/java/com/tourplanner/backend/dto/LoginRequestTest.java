package com.tourplanner.backend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void constructorWithArgs() {
        LoginRequest request = new LoginRequest("testuser", "password123");

        assertEquals("testuser", request.getUsernameOrEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void defaultConstructor() {
        LoginRequest request = new LoginRequest();
        assertNotNull(request);
    }

    @Test
    void gettersAndSettersWork() {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("user@example.com");
        request.setPassword("secretpass");

        assertEquals("user@example.com", request.getUsernameOrEmail());
        assertEquals("secretpass", request.getPassword());
    }
}
