package com.tourplanner.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourRequest {

    @NotBlank(message = "Tour name is required")
    private String name;

    private String description;

    @NotBlank(message = "Start location is required")
    private String from;

    @NotBlank(message = "End location is required")
    private String to;

    @NotBlank(message = "Transport type is required")
    private String transportType;

    @NotNull(message = "Distance is required")
    @Positive(message = "Distance must be positive")
    private Double distance;

    @NotNull(message = "Estimated time (in minutes) is required")
    @Positive(message = "Estimated time must be positive")
    private Long estimatedTimeMinutes;
}
