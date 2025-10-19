package com.tablehub.thbackend.websocket;

import com.tablehub.thbackend.service.implementations.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AuthChannelInterceptor.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            logger.info("Handling CONNECT command for new WebSocket session.");
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                logger.debug("Attempting to validate JWT token.");
                if (jwtService.validateToken(token)) {
                    String username = jwtService.getUserNameFromJwtToken(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    accessor.setUser(auth); // Principal for WebSocket session
                    logger.info("Successfully authenticated user '{}' for WebSocket session.", username);
                } else {
                    logger.error("Invalid JWT token provided. Denying connection.");
                    throw new IllegalArgumentException("Invalid JWT token");
                }
            } else {
                logger.error("Missing or invalid 'Authorization: Bearer' header. Denying connection.");
                throw new IllegalArgumentException("Missing Authorization header");
            }
        }
        return message;
    }
}
