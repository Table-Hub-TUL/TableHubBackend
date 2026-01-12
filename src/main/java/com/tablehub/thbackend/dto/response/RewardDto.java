package com.tablehub.thbackend.dto.response;

import com.tablehub.thbackend.dto.types.AddressDto;
import com.tablehub.thbackend.model.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardDto {
    private Long id;
    private String title;
    private String additionalDescription;
    private Image image;
    private String restaurantName;
    private AddressDto restaurantAddress;
    private boolean redeemed;
    private int cost;
}