package com.tablehub.thbackend.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {
    
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // Access the STOMP headers
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Extract headers sent by client
            String username = accessor.getFirstNativeHeader("username");
            String password = accessor.getFirstNativeHeader("password");

            // Very basic authentication (for now)
            if (isValid(username, password)) {
                // Set user info into the WebSocket session
                accessor.setUser(new StompPrincipal(username));
            } else {
                throw new IllegalArgumentException("Invalid username or password!");
            }
        }

        return message;
    }

    private boolean isValid(String username, String password) {
        // In real app, check database or authentication provider!
        return "alice".equals(username) && "hunter2".equals(password);
    }

    // Inner class for simple Principal implementation
    private static class StompPrincipal implements Principal {
        private final String name;

        public StompPrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}
