package com.tablehub.thbackend.service.implementations;

import com.tablehub.thbackend.dto.request.RestaurantFilterRequest;
import com.tablehub.thbackend.dto.response.RewardDto;
import com.tablehub.thbackend.model.Restaurant;
import com.tablehub.thbackend.model.RestaurantSection;
import com.tablehub.thbackend.repo.RestaurantRepository;
import com.tablehub.thbackend.repo.RewardRepository;
import com.tablehub.thbackend.repo.spec.RestaurantSpecification;
import com.tablehub.thbackend.service.interfaces.RestaurantDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final RewardRepository rewardRepository;
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
    @Transactional(readOnly = true)
    public List<Restaurant> findRestaurantsByCriteria(RestaurantFilterRequest criteria) {
        Specification<Restaurant> spec = RestaurantSpecification.byCriteria(criteria);
        return restaurantRepository.findAll(spec,
                        Pageable.ofSize(criteria.getRestaurantAmount())).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Restaurant> getRestaurantById(Long id) {
        Optional<Restaurant> restaurant = restaurantRepository.findByIdWithSections(id);

        if (restaurant.isPresent()) {
            List<RestaurantSection> sectionsWithTables =
                    restaurantRepository.findSectionsWithTablesByRestaurantId(id);
            restaurant.get().setSections(sectionsWithTables);
        }

        return restaurant;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RewardDto> getRewardsForRestaurant(Long restaurantId) {
        return rewardRepository.findAllByRestaurantId(restaurantId).stream()
                .map(reward -> new RewardDto(
                        reward.getId(),
                        reward.getTitle(),
                        reward.getAdditionalDescription(),
                        reward.getImage(),
                        reward.getRestaurant().getName(),
                        null,
                        false,
                        reward.getCost()
                )).collect(Collectors.toList());
    }
}