package com.dateguide.messaging.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

@FunctionalInterface
public interface MessageHandler<K, V> {
    void handle(ConsumerRecord<K, V> record);
}
