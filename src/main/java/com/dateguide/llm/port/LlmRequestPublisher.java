package com.dateguide.llm.port;

import com.dateguide.llm.dto.LlmRequest;

public interface LlmRequestPublisher {
    void publish(LlmRequest request);
}
