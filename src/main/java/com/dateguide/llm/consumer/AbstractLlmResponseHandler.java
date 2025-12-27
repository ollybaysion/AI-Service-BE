package com.dateguide.llm.consumer;

import com.dateguide.llm.dto.LlmResponse;
import com.dateguide.messaging.kafka.consumer.AbstractKafkaMessageHandler;
import com.dateguide.messaging.message.GMessage;
import tools.jackson.databind.ObjectMapper;

public abstract class AbstractLlmResponseHandler extends AbstractKafkaMessageHandler {
    public AbstractLlmResponseHandler(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    protected final void handleMessage(GMessage<?> message) {
        LlmResponse response = (LlmResponse) message.payload();
        handleLlmResponse(response);
    }

    protected abstract void handleLlmResponse(LlmResponse response);
}
