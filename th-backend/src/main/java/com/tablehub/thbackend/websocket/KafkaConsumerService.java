package com.tablehub.thbackend.websocket;

import com.tablehub.thbackend.dto.websocket.TableUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Listens for table status updates from Kafka and broadcasts them via WebSocket.
     * The topic pattern listens to all region-specific topics (e.g., topic/tables.region-a.etc).
     * Clients should subscribe to STOMP destination: /topic/tables.region-a.etc
     */
    @KafkaListener(groupId = "websocket-broadcast-group",
            topicPattern = ".*")
    public void handleTableUpdate(TableUpdateEvent event) {
        logger.info("Consumed Kafka message: Table ID {} in region {} updated to {}",
                event.getTableID(), event.getRegion(), event.getTableStatus());

        String destination = String.format("/topic/%s", event.getRegion());

        messagingTemplate.convertAndSend(destination, event);
        logger.info("Broadcasted table update to WebSocket destination: {}", destination);
    }
}