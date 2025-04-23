package com.tablehub.thbackend;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MockWebSocketClient extends WebSocketClient {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final CountDownLatch latch;
    private String receivedMessage;

    public MockWebSocketClient(URI serverUri) {
        super(serverUri);
        this.latch = new CountDownLatch(1);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        HashMap<String, Object> header = new HashMap<>();
        header.put("messageId", 1);
        header.put("correlationId", null);
        header.put("sender", "client");
        header.put("type", "LOGIN_REQUEST");
        header.put("accessToken", null);
        header.put("timestamp", 1697030400000L);

        HashMap<String, Object> body = new HashMap<>();
        body.put("username", "asdf");
        body.put("password", "maslo");

        HashMap<String, Object> message = new HashMap<>();
        message.put("header", header);
        message.put("body", body);

        try {
            send(objectMapper.writeValueAsString(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message) {
        receivedMessage = message;
        latch.countDown();
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // Handle close event if needed
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public String getReceivedMessage() {
        return receivedMessage;
    }

    public void awaitMessage(long timeout, TimeUnit unit) throws InterruptedException {
        latch.await(timeout, unit);
    }

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        MockWebSocketClient client = new MockWebSocketClient(new URI("ws://localhost:8080/ws"));
        client.connectBlocking();
        client.awaitMessage(5, TimeUnit.SECONDS);
        System.out.println("Received message: " + client.getReceivedMessage());
        client.close();
    }
}
