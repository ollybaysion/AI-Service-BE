package com.dateguide.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RedisUtilTest {

    private static RedisClient client;
    private RedisUtil redis;

    @BeforeAll
    static void beforeAll() {
        RedisURI uri = RedisURI.builder()
                .withHost("127.0.0.1")
                .withPort(6379)
                .withDatabase(0)
                .build();

        client = RedisClient.create(uri);
    }

    @AfterAll
    static void afterAll() {
        client.shutdown();
    }

    @BeforeEach
    void setup() {
        redis = new RedisUtil(client);
        assertEquals("PONG", redis.ping());
    }

    @AfterEach
    void tearDown() {
        redis.close();
    }

    @Test
    void hset_hget_shouldWork() {
        String key = "test:redisutil:1";

        assertTrue(redis.hset(key, "quality_score", "0.85"));
        assertEquals("0.85", redis.hget(key, "quality_score"));

        redis.del(key);
    }

    @Test
    void hmgetAsList_shouldReturnNullForMissingField() {
        String key = "test:redisutil:2";

        redis.hset(key, "quality_score", "0.91");

        List<String> values = redis.hmgetAsList(
                key,
                "quality_score",
                "updated_at"
        );

        assertEquals(2, values.size());
        assertEquals("0.91", values.get(0));
        assertNull(values.get(1));

        redis.del(key);
    }

    @Test
    void expire_shouldRemoveKey() throws Exception {
        String key = "test:redisutil:3";

        redis.hset(key, "quality_score", "0.5");
        assertTrue(redis.expire(key, Duration.ofSeconds(1)));

        Thread.sleep(1200);

        assertFalse(redis.exists(key));
    }

    @Test
    void hsetAll_shouldWork() {
        String key = "test:redisutil:4";

        redis.hsetAll(key, Map.of(
                "quality_score", "0.77",
                "updated_at", "123"
        ));

        assertEquals("0.77", redis.hgetAll(key).get("quality_score"));
        assertEquals("123", redis.hgetAll(key).get("updated_at"));

        redis.del(key);
    }
}