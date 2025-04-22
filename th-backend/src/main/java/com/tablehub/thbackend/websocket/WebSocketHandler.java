package com.tablehub.thbackend.websocket;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

import com.tablehub.thbackend.service.UserService;
import com.tablehub.thbackend.model.User;

public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserService userService;

    public WebSocketHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        //test only
        System.out.println("📥 Server received: " + message.getPayload());
        
        JsonNode jsonNode = mapper.readTree(message.getPayload());
        String type = jsonNode.get("type").asText();
        
        switch (type) {
            case "LOGIN_REQUEST" -> handleLoginRequest(session, jsonNode);
            case "LOGOUT_REQUEST" -> handleLogoutRequest(session, jsonNode);
            default -> System.out.println("Unknown message type: " + type);
        }
    }

    private void handleLoginRequest(WebSocketSession session, JsonNode jsonNode) {
        // Handle login request
        String username = jsonNode.get("body").get("username").asText();
        String password = jsonNode.get("body").get("password").asText();

        Optional<User> user = userService.authenticate(username, password);
        String responseMessage;

        if(user.isPresent()) {
            responseMessage = "Login successful for user: " + username; // placeholder for actual response
        } else {
            responseMessage = "Login unsuccessful for user: " + username; // placeholder for actual response
        }

        try {
            session.sendMessage(new TextMessage(responseMessage)); //placeholder for actual response
        } catch (IOException e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }

    private void handleLogoutRequest(WebSocketSession session, JsonNode jsonNode) {
        // Handle logout request
        String username = jsonNode.get("body").get("username").asText();

        // Perform logout logic here
        String responseMessage = "Logout successful for user: " + username; // placeholder for actual response

        try {
            session.sendMessage(new TextMessage(responseMessage));
        } catch (IOException e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }
}