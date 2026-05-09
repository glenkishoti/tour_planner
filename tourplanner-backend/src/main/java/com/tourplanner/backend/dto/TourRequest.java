package com.tourplanner.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Objects;

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

    private Double distance;

    private Long estimatedTimeMinutes;

    public TourRequest() {}

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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TourRequest request = new TourRequest();

        public Builder name(String name) { request.setName(name); return this; }
        public Builder description(String description) { request.setDescription(description); return this; }
        public Builder from(String from) { request.setFrom(from); return this; }
        public Builder to(String to) { request.setTo(to); return this; }
        public Builder transportType(String transportType) { request.setTransportType(transportType); return this; }
        public Builder distance(Double distance) { request.setDistance(distance); return this; }
        public Builder estimatedTimeMinutes(Long estimatedTimeMinutes) { request.setEstimatedTimeMinutes(estimatedTimeMinutes); return this; }

        public TourRequest build() { return request; }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TourRequest that = (TourRequest) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(from, that.from) &&
                Objects.equals(to, that.to) &&
                Objects.equals(transportType, that.transportType) &&
                Objects.equals(distance, that.distance) &&
                Objects.equals(estimatedTimeMinutes, that.estimatedTimeMinutes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, from, to, transportType, distance, estimatedTimeMinutes);
    }
}
