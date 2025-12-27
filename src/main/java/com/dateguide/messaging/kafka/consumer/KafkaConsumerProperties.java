package com.dateguide.messaging.kafka.consumer;

import java.time.Duration;
import java.util.List;

public record KafkaConsumerProperties(
        String bootstrapServers,
        String groupId,
        List<String> topicList,
        String clientId,
        Duration pollTimeout,
        boolean enableAutoCommit,
        KafkaPoller.CommitMode commitMode
) {
}
