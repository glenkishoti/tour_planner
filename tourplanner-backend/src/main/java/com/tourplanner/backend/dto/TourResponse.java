package com.tourplanner.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourResponse {
    private Long id;
    private String name;
    private String description;
    private String from;
    private String to;
    private String transportType;
    private Double distance;
    private Long estimatedTimeMinutes;
    private String imagePath;
}
