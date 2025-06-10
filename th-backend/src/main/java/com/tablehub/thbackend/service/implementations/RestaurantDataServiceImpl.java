package com.tablehub.thbackend.service.implementations;

import com.tablehub.thbackend.model.Restaurant;
import com.tablehub.thbackend.model.RestaurantSection;
import com.tablehub.thbackend.repo.RestaurantRepository;
import com.tablehub.thbackend.service.interfaces.RestaurantDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RestaurantDataServiceImpl implements RestaurantDataService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public List<Restaurant> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAllWithSections();

        if (!restaurants.isEmpty()) {
            List<Long> restaurantIds = restaurants.stream()
                    .map(Restaurant::getId)
                    .toList();

            List<RestaurantSection> sectionsWithTables =
                    restaurantRepository.findSectionsWithTablesByRestaurantIds(restaurantIds);

            Map<Long, List<RestaurantSection>> sectionsByRestaurant = sectionsWithTables.stream()
                    .collect(Collectors.groupingBy(section -> section.getRestaurant().getId()));

            restaurants.forEach(restaurant -> {
                List<RestaurantSection> sections = sectionsByRestaurant.get(restaurant.getId());
                if (sections != null) {
                    restaurant.setSections(sections);
                }
            });
        }

        return restaurants;
    }

    @Override
    public Optional<Restaurant> getRestaurantById(Long id) {
        Optional<Restaurant> restaurant = restaurantRepository.findByIdWithSections(id);

        if (restaurant.isPresent()) {
            List<RestaurantSection> sectionsWithTables =
                    restaurantRepository.findSectionsWithTablesByRestaurantId(id);
            restaurant.get().setSections(sectionsWithTables);
        }

        return restaurant;
    }
}