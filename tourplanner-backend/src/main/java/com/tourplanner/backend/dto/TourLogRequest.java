package com.tourplanner.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.Objects;

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

    public TourLogRequest() {}

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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TourLogRequest request = new TourLogRequest();

        public Builder dateTime(LocalDateTime dateTime) { request.setDateTime(dateTime); return this; }
        public Builder comment(String comment) { request.setComment(comment); return this; }
        public Builder difficulty(Integer difficulty) { request.setDifficulty(difficulty); return this; }
        public Builder totalDistance(Double totalDistance) { request.setTotalDistance(totalDistance); return this; }
        public Builder totalTimeMinutes(Long totalTimeMinutes) { request.setTotalTimeMinutes(totalTimeMinutes); return this; }
        public Builder rating(Integer rating) { request.setRating(rating); return this; }

        public TourLogRequest build() { return request; }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TourLogRequest that = (TourLogRequest) o;
        return Objects.equals(dateTime, that.dateTime) &&
                Objects.equals(comment, that.comment) &&
                Objects.equals(difficulty, that.difficulty) &&
                Objects.equals(totalDistance, that.totalDistance) &&
                Objects.equals(totalTimeMinutes, that.totalTimeMinutes) &&
                Objects.equals(rating, that.rating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, comment, difficulty, totalDistance, totalTimeMinutes, rating);
    }
}
