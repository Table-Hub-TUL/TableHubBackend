package com.tablehub.thbackend;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebSocketIntegrationTest {

    @Test
    public void testWebSocketEcho() throws Exception {
        String serverUri = "ws://localhost:8080/ws";

        MockWebSocketClient client = new MockWebSocketClient(new URI(serverUri));

        boolean connected = client.connectBlocking();
        assertTrue(connected, "WebSocket should connect");

        client.awaitMessage(5, TimeUnit.SECONDS);
        String response = client.getReceivedMessage();

        assertEquals("{\"header\":{\"sender\":\"server\",\"messageId\":1,\"correlationId\":null,\"type\":\"LOGIN_SUCCESS\",\"accessToken\":\"ASDFASDFASDF\",\"timestamp\":1697030400000},\"body\":{\"expiresIn\":1000,\"password\":\"maslo\",\"username\":\"asdf\",\"points\":100}}", response);

        client.close();
    }
}
