package com.dateguide.recommendation.port.in;

import com.dateguide.llm.dto.LlmResponse;

public interface LlmResponseUseCase {
    void onResponse(LlmResponse response);
}
