package com.dateguide.llm.kafka;

import com.dateguide.messaging.kafka.consumer.KafkaConsumerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.llm-response")
public record LlmResponseKafkaProperties(
        KafkaConsumerProperties consumer
) {
}
