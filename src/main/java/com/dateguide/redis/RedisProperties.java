package com.dateguide.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redis")
public record RedisProperties(
    String host,
    int port,
    String password,
    int database,
    int timeoutMs
) {
    public RedisProperties {
        if (host == null || host.isBlank()) host = "localhost";
        if (port <= 0) port = 6379;
        if (database < 0) database = 0;
        if (timeoutMs <= 0) timeoutMs = 2000;
        if (password == null) password = "";
    }

    public boolean hasPassword() {
        return password != null && !password.isBlank();
    }
}
