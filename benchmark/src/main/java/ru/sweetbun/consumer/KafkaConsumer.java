/*
package ru.sweetbun.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "benchmark_topic", groupId = "benchmark_group")
    public void consumeMessage(String message) {
        log.info("Kafka Consumer received: {}", message);
    }
}*/
