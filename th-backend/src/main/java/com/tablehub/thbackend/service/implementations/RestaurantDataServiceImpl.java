package com.tablehub.thbackend.service.implementations;

import com.tablehub.thbackend.model.Restaurant;
import com.tablehub.thbackend.repo.RestaurantRepository;
import com.tablehub.thbackend.service.interfaces.RestaurantDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RestaurantDataServiceImpl implements RestaurantDataService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    public Optional<Restaurant> getRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }
}