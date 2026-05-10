package com.tourplanner.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageResponse {

    public String message;

    public MessageResponse(String message) {
        this.message = message;
    }

}
