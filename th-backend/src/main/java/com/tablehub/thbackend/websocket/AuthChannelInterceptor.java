package com.tablehub.thbackend.websocket;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        // Access the STOMP headers
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Extract headers sent by client
            String token = accessor.getFirstNativeHeader("Authorization");

            // Validate the JWT token
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                try {
                    // Decode the JWT token
                    Jwt jwt = jwtDecoder.decode(jwtToken);
                    // Set user info into the WebSocket session
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            jwt.getSubject(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

                    accessor.setUser(authentication);
                } catch (JwtException e) {
                    throw new IllegalArgumentException("Invalid JWT token!", e);
                }
            } else {
                throw new IllegalArgumentException("Missing or invalid Authorization header!");
            }
        }
        return message;
    }
}
