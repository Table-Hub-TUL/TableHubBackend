package com.tablehub.thbackend.service.interfaces;

import org.locationtech.jts.geom.Point;

public interface GeocodingService {
    Point getCoordinatesFromAddress(String address);
}
