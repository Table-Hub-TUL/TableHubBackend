package com.tablehub.thbackend.service.implementations;

import com.tablehub.thbackend.model.Location;
import com.tablehub.thbackend.dto.response.RestaurantResponseDto;
import com.tablehub.thbackend.dto.response.RestaurantsResponse;
import com.tablehub.thbackend.model.Address;
import com.tablehub.thbackend.model.Restaurant;
import com.tablehub.thbackend.repo.RestaurantRepository;
import com.tablehub.thbackend.service.interfaces.RestaurantDataService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RestaurantDataServiceImpl implements RestaurantDataService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public RestaurantsResponse getAllRestaurants() {
        List<RestaurantResponseDto> restaurants = getAllRestaurantsAsList();
        return new RestaurantsResponse(restaurants);
    }

    @Override
    public List<RestaurantResponseDto> getAllRestaurantsAsList() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurants.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantResponseDto getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with id: " + id));
        return convertToDto(restaurant);
    }

    private RestaurantResponseDto convertToDto(Restaurant restaurant) {
        RestaurantResponseDto dto = new RestaurantResponseDto();

        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setAddress(buildAddressString(restaurant.getAddress()));
        dto.setLocation(convertPointToLocation(restaurant.getLocation()));
        dto.setCuisine(convertCuisineToList(restaurant.getCuisineName()));
        dto.setRating(restaurant.getRating());

        return dto;
    }

    private String buildAddressString(Address address) {
        if (address == null) {
            return "Address not available";
        }

        StringBuilder addressBuilder = new StringBuilder();
        addressBuilder.append(address.getStreetNumber());

        if (address.getApartmentNumber() != null) {
            addressBuilder.append("/").append(address.getApartmentNumber());
        }

        addressBuilder.append(" ").append(address.getStreet());
        addressBuilder.append(", ").append(address.getCity());

        return addressBuilder.toString();
    }

    private Location convertPointToLocation(Point point) {
        if (point == null) {
            return new Location(0.0, 0.0);
        }

        // PostGIS Point: X = longitude, Y = latitude
        double latitude = point.getY();
        double longitude = point.getX();

        return new Location(latitude, longitude);
    }

    private List<String> convertCuisineToList(com.tablehub.thbackend.model.CuisineName cuisineName) {
        if (cuisineName == null) {
            return List.of();
        }

        String cuisineString = cuisineName.name().toLowerCase();
        return List.of(cuisineString);
    }
}