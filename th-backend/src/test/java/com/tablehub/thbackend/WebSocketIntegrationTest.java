package com.tablehub.thbackend;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebSocketIntegrationTest {

    @Test
    public void testWebSocketEcho() throws Exception {
        String serverUri = "ws://localhost:8080/ws";

        CountDownLatch latch = new CountDownLatch(1);
        BlockingQueue<String> messages = new LinkedBlockingQueue<>();

        WebSocketClient client = new WebSocketClient(new URI(serverUri)) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                send("Hello server");
            }

            @Override
            public void onMessage(String message) {
                System.out.println("📩 Client received: " + message); // <-- log it!
                messages.offer(message);
                latch.countDown();
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {}

            @Override
            public void onError(Exception ex) {
                fail("WebSocket error: " + ex.getMessage());
            }
        };

        boolean connected = client.connectBlocking(3, TimeUnit.SECONDS);
        assertTrue(connected, "WebSocket should connect");

        boolean success = latch.await(5, TimeUnit.SECONDS);
        assertTrue(success, "Should receive message within timeout");

        String response = messages.poll(1, TimeUnit.SECONDS);
        assertEquals("Echo: Hello server", response);
    }
}
