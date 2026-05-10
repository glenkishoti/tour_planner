package com.tourplanner.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final TourLogResponse response = new TourLogResponse();

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
