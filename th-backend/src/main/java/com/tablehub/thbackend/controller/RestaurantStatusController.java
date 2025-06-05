package com.tablehub.thbackend.controller;

import com.tablehub.thbackend.dto.response.RestaurantStatusDto;
import com.tablehub.thbackend.dto.request.UpdateTableStatusRequest;
import com.tablehub.thbackend.dto.response.UpdateTableStatusResponse;
import com.tablehub.thbackend.service.interfaces.RestaurantService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class RestaurantStatusController {

    private final RestaurantService restaurantService;

    public RestaurantStatusController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    /**
     * Client SENDs to /app/initialStatus (an empty frame).
     * We fetch the snapshot via the service and send it back only to this user’s session
     * on /user/queue/initialStatus. - hints on what to do on client side
     */
    @MessageMapping("/initialStatus")
    @SendToUser("/queue/initialStatus")
    public List<RestaurantStatusDto> sendInitialStatus() {
        return restaurantService.getAllRestaurantStatuses();
    }

    /**
     * Client SENDs to /app/updateTableStatus with a JSON body matching UpdateTableStatusRequest.
     * We call the service to persist, broadcast the change, and return a response DTO
     * on /user/queue/updateTableStatus. - hints on what to do on client side
     */
    @MessageMapping("/updateTableStatus")
    @SendToUser("/queue/updateTableStatus")
    public UpdateTableStatusResponse handleUpdate(UpdateTableStatusRequest req,
                                                  Principal user) {
        return restaurantService.updateTableStatus(req, user.getName());
    }
}
