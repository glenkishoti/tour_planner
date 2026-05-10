package com.tourplanner.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class ImportExportDTO {
    private String exportVersion;
    private LocalDateTime exportDate;
    private List<TourExportData> tours;

    @Setter
    @Getter
    public static class TourExportData {
        private String name;
        private String description;
        private String from;
        private String to;
        private String transportType;
        private Double distance;
        private Long estimatedTimeMinutes;
        private String imagePath;
        private List<TourLogExportData> logs;

    }

    @Setter
    @Getter
    public static class TourLogExportData {
        private LocalDateTime dateTime;
        private String comment;
        private Integer difficulty;
        private Double totalDistance;
        private Long totalTimeMinutes;
        private Integer rating;

    }
}
