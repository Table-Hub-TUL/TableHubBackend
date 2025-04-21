package com.tablehub.thbackend;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class MockWebSocketClient extends WebSocketClient {

    private final CountDownLatch latch;
    private String receivedMessage;

    public MockWebSocketClient(URI serverUri) {
        super(serverUri);
        this.latch = new CountDownLatch(1);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        send("Hello server");
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
