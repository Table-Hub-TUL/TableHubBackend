package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.dto.TableStatusEnum;
import com.tablehub.thbackend.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

}
