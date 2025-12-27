package com.dateguide.llm.port.out;

import com.dateguide.llm.dto.LlmRequest;

public interface LlmRequestPublisher {
    void publish(LlmRequest request);
}
