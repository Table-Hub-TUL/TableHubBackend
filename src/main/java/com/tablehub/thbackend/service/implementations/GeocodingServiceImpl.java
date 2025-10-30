package com.tablehub.thbackend.service.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import com.tablehub.thbackend.service.interfaces.GeocodingService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeocodingServiceImpl implements GeocodingService {

    private static final Logger logger = LoggerFactory.getLogger(GeocodingServiceImpl.class);
    private final RestTemplate restTemplate;

    public GeocodingServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Point getCoordinatesFromAddress(String address) {
        String url = "https://nominatim.openstreetmap.org/search";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("q", address)
                .queryParam("format", "json")
                .queryParam("limit", 1);

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "TableHubApp/1.0");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode[]> response = restTemplate.exchange(
                    builder.build().toUri(),
                    HttpMethod.GET,
                    entity,
                    JsonNode[].class
            );

            JsonNode[] results = response.getBody();

            if (results != null && results.length > 0) {
                JsonNode firstResult = results[0];
                double lon = firstResult.get("lon").asDouble();
                double lat = firstResult.get("lat").asDouble();

                logger.info("Successfully geocoded address '{}' to [lon={}, lat={}]", address, lon, lat);
                return new GeometryFactory().createPoint(new Coordinate(lon, lat));
            } else {
                logger.warn("No results found for address: {}", address);
                return null;
            }
        } catch (Exception e) {
            logger.error("An error occurred during geocoding for address: {}", address, e);
            return null;
        }
    }
}