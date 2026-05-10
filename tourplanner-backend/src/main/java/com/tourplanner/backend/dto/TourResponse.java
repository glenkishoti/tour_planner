package com.tourplanner.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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
    private Integer popularity;
    private Double childFriendliness;

    public TourResponse() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final TourResponse response = new TourResponse();

        public Builder id(Long id) { response.setId(id); return this; }
        public Builder name(String name) { response.setName(name); return this; }
        public Builder description(String description) { response.setDescription(description); return this; }
        public Builder from(String from) { response.setFrom(from); return this; }
        public Builder to(String to) { response.setTo(to); return this; }
        public Builder transportType(String transportType) { response.setTransportType(transportType); return this; }
        public Builder distance(Double distance) { response.setDistance(distance); return this; }
        public Builder estimatedTimeMinutes(Long estimatedTimeMinutes) { response.setEstimatedTimeMinutes(estimatedTimeMinutes); return this; }
        public Builder imagePath(String imagePath) { response.setImagePath(imagePath); return this; }
        public Builder popularity(Integer popularity) { response.setPopularity(popularity); return this; }
        public Builder childFriendliness(Double childFriendliness) { response.setChildFriendliness(childFriendliness); return this; }

        public TourResponse build() { return response; }
    }
}
