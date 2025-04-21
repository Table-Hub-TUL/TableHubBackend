package com.tablehub.thbackend.websocket;

import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketHandler extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        //test only
        System.out.println("📥 Server received: " + message.getPayload());

        String clientMsg = message.getPayload();

        System.out.println("📤 Server sending: " + clientMsg);

        session.sendMessage(new TextMessage("Echo: " + clientMsg));
    }
}