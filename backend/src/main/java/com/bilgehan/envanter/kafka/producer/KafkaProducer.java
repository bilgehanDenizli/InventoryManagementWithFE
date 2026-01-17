package com.bilgehan.envanter.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public <T> void send(String topic, T payload) {
        kafkaTemplate.send(topic, UUID.randomUUID().toString(), payload);
    }

    public <T> void send(String topic, String key, T payload) {
        kafkaTemplate.send(topic, key, payload);
    }

}
