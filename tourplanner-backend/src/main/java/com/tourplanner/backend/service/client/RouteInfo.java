package com.tourplanner.backend.service.client;

public class RouteInfo {
    private Double distance;
    private Long durationInSeconds;
    private String geometry;

    public RouteInfo() {}

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    public Long getDurationInSeconds() { return durationInSeconds; }
    public void setDurationInSeconds(Long durationInSeconds) { this.durationInSeconds = durationInSeconds; }

    public String getGeometry() { return geometry; }
    public void setGeometry(String geometry) { this.geometry = geometry; }
}
