package com.dateguide.messaging.kafka.consumer;

import com.dateguide.messaging.message.GMessage;
import com.dateguide.messaging.consumer.MessageHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import tools.jackson.databind.ObjectMapper;

public abstract class AbstractKafkaMessageHandler implements MessageHandler<String, String> {

    private final ObjectMapper mapper;

    protected AbstractKafkaMessageHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * ConsumerRecord 에서 GMessage 로 변환 후 적절한 Handler 호출
     */
    @Override
    public final void handle(ConsumerRecord<String, String> record) {
        GMessage<?> message;
        try {
            message = mapper.readValue(record.value(), GMessage.class);
            handleMessage(message);
        } catch (Exception e) {
            return;
        }
    }

    protected abstract void handleMessage(GMessage<?> message);

}
