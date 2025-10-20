package com.tablehub.thbackend.repo.spec;

import com.tablehub.thbackend.dto.request.RestaurantFilterRequest;
import com.tablehub.thbackend.model.Restaurant;
import jakarta.persistence.criteria.Expression;
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

            if (criteria.getCuisine() != null && !criteria.getCuisine().isEmpty()) {
                predicates.add(root.get("cuisineName").in(criteria.getCuisine()));
            }

            if (criteria.getRating() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), criteria.getRating()));
            }

            if (criteria.getUserLat() != null && criteria.getUserLon() != null && criteria.getRadius() != null && criteria.getRadius() > 0) {
                // SRID 4326 is standard for WGS 84 GPS coordinates.
                GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
                Point userLocation = geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(criteria.getUserLon(), criteria.getUserLat()));

                // ST_GeomFromText takes geometry string and SRID as separate parameters
                Expression<Object> userLocationGeom = criteriaBuilder.function(
                        "ST_GeomFromText",
                        Object.class,
                        criteriaBuilder.literal(userLocation.toText()),
                        criteriaBuilder.literal(4326)
                );

                predicates.add(criteriaBuilder.isTrue(
                        criteriaBuilder.function(
                                "ST_DWithin",
                                Boolean.class,
                                criteriaBuilder.function("geography", Object.class, root.get("location")),
                                criteriaBuilder.function("geography", Object.class, userLocationGeom),
                                criteriaBuilder.literal(criteria.getRadius() * 1000)
                        )
                ));
            }
            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}