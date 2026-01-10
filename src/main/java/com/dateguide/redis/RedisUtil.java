package com.dateguide.redis;

import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.annotation.PreDestroy;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class RedisUtil {

    private final StatefulRedisConnection<String, String> conn;
    private final RedisCommands<String, String> cmd;


    public RedisUtil(RedisClient client) {
        this.conn = client.connect();
        this.cmd = conn.sync();
    }

    public String ping() {
        return cmd.ping();
    }

    @PreDestroy
    public void close() {
        conn.close();
    }

    public boolean exists(String key) {
        return cmd.exists(key) > 0;
    }

    public boolean expire(String key, Duration ttl) {
        return cmd.expire(key, ttl.toSeconds());
    }

    public boolean del(String key) {
        return cmd.del(key) > 0;
    }

    public boolean hset(String key, String field, String value) {
        return cmd.hset(key, field, value);
    }

    public void hsetAll(String key, Map<String, String> fields) {
        cmd.hset(key, fields);
    }

    public String hget(String key, String field) {
        return cmd.hget(key, field);
    }

    public List<String> hmgetAsList(String key, String... fields) {
        List<KeyValue<String, String>> kvs = cmd.hmget(key, fields);
        return kvs.stream()
                .map(kv -> kv.hasValue() ? kv.getValue() : null)
                .toList();
    }

    public Map<String, String> hgetAll(String key) {
        return cmd.hgetall(key);
    }
}
