package com.dateguide.recommendation.application.handler;

import com.dateguide.llm.consumer.AbstractLlmResponseHandler;
import com.dateguide.llm.dto.LlmResponse;
import com.dateguide.recommendation.port.in.LlmResponseUseCase;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class RecommendLlmResponseHandler extends AbstractLlmResponseHandler {

    private final LlmResponseUseCase useCase;

    public RecommendLlmResponseHandler(ObjectMapper mapper, LlmResponseUseCase useCase) {
        super(mapper);

        this.useCase = useCase;
    }

    @Override
    protected void handleLlmResponse(LlmResponse response) {
        useCase.onResponse(response);
    }
}
