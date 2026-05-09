package com.tourplanner.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ImportExportDTO {
    private String exportVersion;
    private LocalDateTime exportDate;
    private List<TourExportData> tours;

    public String getExportVersion() { return exportVersion; }
    public void setExportVersion(String exportVersion) { this.exportVersion = exportVersion; }

    public LocalDateTime getExportDate() { return exportDate; }
    public void setExportDate(LocalDateTime exportDate) { this.exportDate = exportDate; }

    public List<TourExportData> getTours() { return tours; }
    public void setTours(List<TourExportData> tours) { this.tours = tours; }

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

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }

        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }

        public String getTransportType() { return transportType; }
        public void setTransportType(String transportType) { this.transportType = transportType; }

        public Double getDistance() { return distance; }
        public void setDistance(Double distance) { this.distance = distance; }

        public Long getEstimatedTimeMinutes() { return estimatedTimeMinutes; }
        public void setEstimatedTimeMinutes(Long estimatedTimeMinutes) { this.estimatedTimeMinutes = estimatedTimeMinutes; }

        public String getImagePath() { return imagePath; }
        public void setImagePath(String imagePath) { this.imagePath = imagePath; }

        public List<TourLogExportData> getLogs() { return logs; }
        public void setLogs(List<TourLogExportData> logs) { this.logs = logs; }
    }

    public static class TourLogExportData {
        private LocalDateTime dateTime;
        private String comment;
        private Integer difficulty;
        private Double totalDistance;
        private Long totalTimeMinutes;
        private Integer rating;

        public LocalDateTime getDateTime() { return dateTime; }
        public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }

        public Integer getDifficulty() { return difficulty; }
        public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }

        public Double getTotalDistance() { return totalDistance; }
        public void setTotalDistance(Double totalDistance) { this.totalDistance = totalDistance; }

        public Long getTotalTimeMinutes() { return totalTimeMinutes; }
        public void setTotalTimeMinutes(Long totalTimeMinutes) { this.totalTimeMinutes = totalTimeMinutes; }

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
    }
}
