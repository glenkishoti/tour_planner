package com.tourplanner.backend.dto;

import java.time.LocalDateTime;

public class TourLogResponse {
    private Long id;
    private LocalDateTime dateTime;
    private String comment;
    private Integer difficulty;
    private Double totalDistance;
    private Long totalTimeMinutes;
    private Integer rating;
    private Long tourId;

    public TourLogResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Long getTourId() { return tourId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TourLogResponse response = new TourLogResponse();

        public Builder id(Long id) { response.setId(id); return this; }
        public Builder dateTime(LocalDateTime dateTime) { response.setDateTime(dateTime); return this; }
        public Builder comment(String comment) { response.setComment(comment); return this; }
        public Builder difficulty(Integer difficulty) { response.setDifficulty(difficulty); return this; }
        public Builder totalDistance(Double totalDistance) { response.setTotalDistance(totalDistance); return this; }
        public Builder totalTimeMinutes(Long totalTimeMinutes) { response.setTotalTimeMinutes(totalTimeMinutes); return this; }
        public Builder rating(Integer rating) { response.setRating(rating); return this; }
        public Builder tourId(Long tourId) { response.setTourId(tourId); return this; }

        public TourLogResponse build() { return response; }
    }
}
