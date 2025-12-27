package com.dateguide.messaging.kafka.publisher;

import com.dateguide.messaging.message.GMessage;
import com.dateguide.messaging.port.out.MessagePublisher;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Component
public class KafkaMessagePublisher implements MessagePublisher, DisposableBean {

    private final KafkaProducer<String, String> producer;
    private final ObjectMapper objectMapper;

    public KafkaMessagePublisher(
            ObjectMapper objectMapper,
            @Value("${kafka.bootstrapServers}") String bootstrapServers
    ) {
        this.objectMapper = objectMapper;

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.RETRIES_CONFIG, 5);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        this.producer = new KafkaProducer<>(props);
    }

    /**
     * message.destination 을 Kafka Topic 으로 사용한다.
     * key 는 message.key 를 사용한다.
     */
    @Override
    public void publish(GMessage<?> message) {
        if (message == null)
            throw new IllegalArgumentException("Message is null");

        if(message.gDestination() == null || message.gDestination().isEmpty())
            throw new IllegalArgumentException("Destination is required");

        try {
            String json = objectMapper.writeValueAsString(message);

            ProducerRecord<String, String> record =
                    new ProducerRecord<>(message.gDestination(), message.key(), json);

            message.headers().forEach((k,v) -> record.headers().add(k, v.getBytes(StandardCharsets.UTF_8)));
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    System.err.println("[KafkaMessagePublisher] publish failed. "
                            + "messageId=" + message.gId()
                            + "topic=" + message.gDestination()
                            + "err=" + exception.getMessage());
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize/publish messageId=" + message.gId(), e);
        }
    }

    @Override
    public void destroy() throws Exception {
        producer.flush();
        producer.close();
    }
}
