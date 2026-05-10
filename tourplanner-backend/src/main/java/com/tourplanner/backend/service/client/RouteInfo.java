package com.tourplanner.backend.service.client;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RouteInfo {
    private Double distance;
    private Long durationInSeconds;
    private String geometry;

    public RouteInfo() {}

}
