package com.tourplanner.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourLogRequest {

    @NotNull(message = "Date and time is required")
    private LocalDateTime dateTime;

    @NotBlank(message = "Comment is required")
    private String comment;

    @NotNull(message = "Difficulty is required")
    @Min(value = 1, message = "Difficulty must be between 1 and 10")
    @Max(value = 10, message = "Difficulty must be between 1 and 10")
    private Integer difficulty;

    @NotNull(message = "Total distance is required")
    @Positive(message = "Total distance must be positive")
    private Double totalDistance;

    @NotNull(message = "Total time (in minutes) is required")
    @Positive(message = "Total time must be positive")
    private Long totalTimeMinutes;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;
}
