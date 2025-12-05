package com.tablehub.thbackend.dto.response;

import com.tablehub.thbackend.dto.types.AddressDto;
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
    // Assuming Image is a URL string for JSON serialization
    private String image;
    private String restaurantName;
    private AddressDto restaurantAddress;
    private boolean redeemed;
}