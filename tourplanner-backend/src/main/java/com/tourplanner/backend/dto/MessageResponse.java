package com.tourplanner.backend.dto;

public class MessageResponse {
    public String message;

    //Constructor
    public MessageResponse(String message) {
        this.message = message;
    }

    //Getters
    public String getMessage() {
        return message;
    }

    //Setters
    public void setMessage(String message) {
        this.message = message;
    }
}
