package com.tourplanner.backend.service.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OpenRouteServiceClient {

    private static final Logger log = LoggerFactory.getLogger(OpenRouteServiceClient.class);
    private static final String BASE_URL = "https://api.openrouteservice.org";
    private static final String DIRECTIONS_ENDPOINT = "/v2/directions/";
    private static final String GEOCODE_ENDPOINT = "/geocode/search";

    @Value("${openrouteservice.api.key:}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RouteInfo getRouteInfo(String from, String to, String transportType) {
        log.info("Fetching route info from '{}' to '{}' via '{}'", from, to, transportType);

        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("OpenRouteService API key not configured, using fallback values");
            return createFallbackRouteInfo(from, to, transportType);
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            double[] fromCoords = geocode(from);
            double[] toCoords = geocode(to);

            if (fromCoords == null || toCoords == null) {
                log.warn("Could not geocode locations, using fallback values");
                return createFallbackRouteInfo(from, to, transportType);
            }

            String profile = mapTransportTypeToProfile(transportType);
            String coordinates = String.format("[[%f,%f],[%f,%f]]",
                    fromCoords[0], fromCoords[1], toCoords[0], toCoords[1]);

            String url = BASE_URL + DIRECTIONS_ENDPOINT + profile;

            HttpPost request = new HttpPost(url);
            request.setHeader("Authorization", apiKey);
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(
                "{\"coordinates\":" + coordinates + "}",
                ContentType.APPLICATION_JSON
            ));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                if (response.getCode() == 200) {
                    return parseRouteResponse(responseBody);
                } else {
                    log.error("OpenRouteService returned error code: {}", response.getCode());
                    return createFallbackRouteInfo(from, to, transportType);
                }
            }
        } catch (Exception e) {
            log.error("Error fetching route info: {}", e.getMessage(), e);
            return createFallbackRouteInfo(from, to, transportType);
        }
    }

    private double[] geocode(String location) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
            String url = BASE_URL + GEOCODE_ENDPOINT + "?api_key=" + apiKey + "&text=" + encodedLocation;

            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                if (response.getCode() == 200) {
                    JsonNode root = objectMapper.readTree(responseBody);
                    JsonNode features = root.path("features");

                    if (features.isArray() && features.size() > 0) {
                        JsonNode firstFeature = features.get(0);
                        JsonNode geometry = firstFeature.path("geometry");
                        JsonNode coordinates = geometry.path("coordinates");

                        if (coordinates.isArray() && coordinates.size() >= 2) {
                            double lon = coordinates.get(0).asDouble();
                            double lat = coordinates.get(1).asDouble();
                            return new double[]{lon, lat};
                        }
                    }
                }
            }
        } catch (IOException | ParseException e) {
            log.error("Error geocoding location '{}': {}", location, e.getMessage());
        }
        return null;
    }

    private RouteInfo parseRouteResponse(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode routes = root.path("routes");

        if (routes.isArray() && routes.size() > 0) {
            JsonNode firstRoute = routes.get(0);
            JsonNode summary = firstRoute.path("summary");

            double distance = summary.path("distance").asDouble();
            double duration = summary.path("duration").asDouble();

            RouteInfo info = new RouteInfo();
            info.setDistance(distance / 1000.0);
            info.setDurationInSeconds((long) duration);
            info.setGeometry(firstRoute.path("geometry").asText());

            return info;
        }

        return null;
    }

    private String mapTransportTypeToProfile(String transportType) {
        if (transportType == null) {
            return "foot-walking";
        }

        String lower = transportType.toLowerCase();
        return switch (lower) {
            case "bike", "bicycle", "cycling" -> "cycling-regular";
            case "car", "driving" -> "driving-car";
            case "running", "jogging", "foot" -> "foot-walking";
            case "hiking" -> "foot-hiking";
            default -> "foot-walking";
        };
    }

    private RouteInfo createFallbackRouteInfo(String from, String to, String transportType) {
        RouteInfo info = new RouteInfo();
        info.setDistance(10.0);
        info.setDurationInSeconds(7200L);
        info.setGeometry("");
        log.info("Using fallback route info: {} km, {} seconds", info.getDistance(), info.getDurationInSeconds());
        return info;
    }
}
