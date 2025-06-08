package com.tablehub.thbackend.websocket;

import com.tablehub.thbackend.service.implementations.RestaurantServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class SubscriptionEventListener {

    private final RestaurantServiceImpl restaurantService;

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal userPrincipal = headerAccessor.getUser();

        if (userPrincipal != null && "/user/queue/restaurant-status".equals(headerAccessor.getDestination())) {
            restaurantService.sendInitialSubscriptionState(userPrincipal.getName());
        }
    }
}