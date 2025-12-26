package com.dateguide.messaging.port;

import com.dateguide.messaging.message.GMessage;

public interface MessagePublisher {

    /**
     * 메시지를 외부 시스템으로 발행한다.
     * 전송 방식은(Kafka, HTTP, DB 등)은 구현체가 결정한다.
     */
    void publish(GMessage<?> message);
}
