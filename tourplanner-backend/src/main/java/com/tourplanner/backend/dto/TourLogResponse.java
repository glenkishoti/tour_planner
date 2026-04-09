package com.tourplanner.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourLogResponse {
    private Long id;
    private LocalDateTime dateTime;
    private String comment;
    private Integer difficulty;
    private Double totalDistance;
    private Long totalTimeMinutes;
    private Integer rating;
    private Long tourId;
}
