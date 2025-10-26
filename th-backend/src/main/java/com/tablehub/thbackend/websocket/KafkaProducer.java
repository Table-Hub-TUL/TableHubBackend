package com.tablehub.thbackend.websocket;

import com.tablehub.thbackend.controller.AdminController;
import com.tablehub.thbackend.dto.websocket.TableUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final KafkaTemplate<String, TableUpdateEvent> kafkaTemplate;

    public void sendMessage(String topic, TableUpdateEvent message) {
        if (topic == null || topic.isEmpty()) {
            throw new IllegalArgumentException("Topic must be specified");
        }
        log.info("Producing message: {} to topic {}", message, topic);
        System.out.println(topic);
        this.kafkaTemplate.send(topic, message);
    }
}
