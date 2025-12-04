package com.tablehub.thbackend.dto.response;

import com.tablehub.thbackend.dto.types.AddressDto;
import com.tablehub.thbackend.model.Reward;
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
    private String imageUrl;
    private String restaurantName;
    private AddressDto restaurantAddress;
    private boolean redeemed;

    public RewardDto(Reward reward, boolean redeemedStatus) {
        this.id = reward.getId();
        this.title = reward.getTitle();
        this.additionalDescription = reward.getAdditionalDescription();
        this.imageUrl = reward.getImageUrl();
        this.redeemed = redeemedStatus;

        if (reward.getRestaurant() != null) {
            this.restaurantName = reward.getRestaurant().getName();
            if (reward.getRestaurant().getAddress() != null) {
                var addr = reward.getRestaurant().getAddress();
                this.restaurantAddress = new AddressDto(
                        addr.getStreetNumber(),
                        addr.getStreet(),
                        addr.getApartmentNumber(),
                        addr.getCity(),
                        addr.getPostalCode(),
                        addr.getCountry()
                );
            }
        }
    }
}