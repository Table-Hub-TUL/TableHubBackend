package com.tablehub.thbackend.websocket;

import com.tablehub.thbackend.service.implementations.RestaurantServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class SubscriptionEventListener {
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionEventListener.class);

    private final RestaurantServiceImpl restaurantService;

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal userPrincipal = headerAccessor.getUser();
        logger.info("Received a new subscription from user '{}' to destination '{}'",
                userPrincipal != null ? userPrincipal.getName() : "anonymous",
                headerAccessor.getDestination());
        if (userPrincipal != null && "/user/queue/restaurant-status".equals(headerAccessor.getDestination())) {
            restaurantService.sendInitialSubscriptionState(userPrincipal.getName());
            logger.info("User '{}' subscribed to restaurant status. Sending initial state.", userPrincipal.getName());
        }
    }
}