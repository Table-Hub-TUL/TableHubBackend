package com.tablehub.thbackend.repo.spec;

import com.tablehub.thbackend.dto.request.RestaurantFilterRequest;
import com.tablehub.thbackend.model.Restaurant;
import jakarta.persistence.criteria.Predicate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RestaurantSpecification {

    public static Specification<Restaurant> byCriteria(RestaurantFilterRequest criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Handle multiple cuisines with IN clause
            if (criteria.getCuisine() != null && !criteria.getCuisine().isEmpty()) {
                predicates.add(root.get("cuisine").in(criteria.getCuisine()));
            }

            if (criteria.getRating() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), criteria.getRating()));
            }

            // PostGIS specific spatial query for radius filtering.
            if (criteria.getUserLat() != null && criteria.getUserLon() != null && criteria.getRadius() != null && criteria.getRadius() > 0) {
                // SRID 4326 is standard for WGS 84 GPS coordinates.
                GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
                Point userLocation = geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(criteria.getUserLon(), criteria.getUserLat()));

                // ST_DWithin function checks if geometries are within a specified distance.
                // This is highly efficient and uses spatial indexes in PostGIS.
                // The distance is in meters, so we convert km to m.
                predicates.add(criteriaBuilder.isTrue(
                        criteriaBuilder.function(
                                "ST_DWithin",
                                Boolean.class,
                                root.get("location"), // The 'Point' column in your Restaurant entity
                                criteriaBuilder.literal(userLocation),
                                criteriaBuilder.literal(criteria.getRadius() * 1000)
                        )
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}