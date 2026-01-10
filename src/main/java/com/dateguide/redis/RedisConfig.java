package com.dateguide.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.ClientResources;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

    private RedisClient redisClient;

    @Bean
    public RedisClient redisClient(RedisProperties props) {
        RedisURI.Builder b = RedisURI.builder()
                .withHost(props.host())
                .withPort(props.port())
                .withDatabase(props.database())
                .withTimeout(Duration.ofMillis(props.timeoutMs()));

        if (props.hasPassword()) {
            b.withPassword(props.password().toCharArray());
        }

        this.redisClient = RedisClient.create(b.build());
        return this.redisClient;
    }

    @Bean
    public RedisUtil redisUtil(RedisClient client) {
        return new RedisUtil(client);
    }

    @PreDestroy
    public void shutdown() {
        if (redisClient != null) {
            redisClient.shutdown();
        }
    }
}
