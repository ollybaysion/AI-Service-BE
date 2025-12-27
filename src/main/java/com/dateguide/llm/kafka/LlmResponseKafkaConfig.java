package com.dateguide.llm.kafka;

import com.dateguide.messaging.kafka.consumer.KafkaConsumerProperties;
import com.dateguide.messaging.kafka.consumer.KafkaPoller;
import com.dateguide.messaging.consumer.MessageHandler;
import com.dateguide.messaging.kafka.consumer.KafkaRunner;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties(LlmResponseKafkaProperties.class)
public class LlmResponseKafkaConfig {

    @Bean
    public KafkaConsumer<String, String> llmResponseKafkaConsumer(LlmResponseKafkaProperties props) {
        KafkaConsumerProperties c = props.consumerProperties();
        
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, c.bootstrapServers());
        p.put(ConsumerConfig.GROUP_ID_CONFIG, c.groupId());

        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        if (c.clientId() != null && !c.clientId().isBlank()) {
            p.put(ConsumerConfig.CLIENT_ID_CONFIG, c.clientId());
        }

        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(c.enableAutoCommit()));
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        return new KafkaConsumer<>(p);
    }

    @Bean
    public KafkaPoller<String, String> llmResponseKafkaPoller(
            KafkaConsumer<String, String> consumer,
            LlmResponseKafkaProperties props,
            MessageHandler<String, String> handler // TODO - Qualifer 추가
    ) {
        KafkaConsumerProperties c = props.consumerProperties();

        Duration pollTimeout = (c.pollTimeout() == null) ? Duration.ofMillis(500) : c.pollTimeout();
        KafkaPoller.CommitMode commitMode = (c.commitMode() == null) ? KafkaPoller.CommitMode.AUTO : c.commitMode();

        return new KafkaPoller<>(
                consumer,
                c.topicList(),
                pollTimeout,
                handler,
                commitMode,
                "llm-response-poller"
        );
    }

    @Bean
    public KafkaRunner llmResponseKafkaRunner(KafkaPoller<String, String> poller) {
        return new KafkaRunner(poller, 0, true);
    }

}
