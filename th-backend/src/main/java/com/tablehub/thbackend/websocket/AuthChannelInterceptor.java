package com.tablehub.thbackend.websocket;

import java.util.List;

import com.tablehub.thbackend.security.jwt.JwtService;
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

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            String token = accessor.getFirstNativeHeader("Authorization");
//
//            if (token != null && token.startsWith("Bearer ")) {
//                token = token.substring(7);
//                if (jwtService.validateToken(token)) {
//                    String username = jwtService.getUserNameFromJwtToken(token);
//                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                    Authentication auth = new UsernamePasswordAuthenticationToken(
//                            userDetails, null, userDetails.getAuthorities());
//                    accessor.setUser(auth); // Principal for WebSocket session
//                } else {
//                    throw new IllegalArgumentException("Invalid JWT token");
//                }
//            } else {
//                throw new IllegalArgumentException("Missing Authorization header");
//            }
//        }
        return message;
    }
}
