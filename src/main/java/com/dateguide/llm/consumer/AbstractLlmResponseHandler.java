package com.dateguide.llm.consumer;

import com.dateguide.llm.dto.LlmResponse;
import com.dateguide.messaging.kafka.consumer.AbstractKafkaMessageHandler;
import com.dateguide.messaging.message.GMessage;
import com.dateguide.recommendation.dto.llm.RecommendLlmResponse;
import tools.jackson.databind.ObjectMapper;

public abstract class AbstractLlmResponseHandler extends AbstractKafkaMessageHandler {
    public AbstractLlmResponseHandler(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    protected final void handleMessage(GMessage<?> message) {
        LlmResponse response = (LlmResponse) mapper.convertValue(message.payload(), RecommendLlmResponse.class);
        handleLlmResponse(response);
    }

    protected abstract void handleLlmResponse(LlmResponse response);
}
