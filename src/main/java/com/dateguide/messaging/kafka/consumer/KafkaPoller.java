package com.dateguide.messaging.kafka.consumer;

import com.dateguide.messaging.consumer.MessageHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaPoller<K, V> implements AutoCloseable {

    public enum CommitMode {
        AUTO,
        MANUAL_SYNC,
        MANUAL_ASYNC
    }

    private final KafkaConsumer<K, V> consumer;
    private final List<String> topicList;
    private final Duration pollTimeout;
    private final MessageHandler<K, V> handler;

    private final CommitMode commitMode;

    private final ExecutorService executor;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile Future<?> task;

    public KafkaPoller(
            KafkaConsumer<K, V> consumer,
            List<String> topicList,
            Duration pollTimeout,
            MessageHandler<K, V> handler,
            CommitMode commitMode,
            String threadName) {
        this.consumer = Objects.requireNonNull(consumer, "consumer");
        this.topicList = Objects.requireNonNull(topicList, "topicList");
        this.pollTimeout = Objects.requireNonNull(pollTimeout, "pollTimeout");
        this.handler = Objects.requireNonNull(handler, "handler");
        this.commitMode = Objects.requireNonNullElse(commitMode, CommitMode.AUTO);

        if (this.topicList.isEmpty()) {
            throw new IllegalArgumentException("topicList must not be empty");
        }

        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, threadName == null ? "kafka-poller" : threadName);
            t.setDaemon(true);
            return t;
        });
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }

        consumer.subscribe(topicList);

        task = executor.submit(this::loop);
    }

    public void loop() {
        try {
            while (running.get()) {
                ConsumerRecords<K, V> records = consumer.poll(pollTimeout);

                if (!records.isEmpty()) {
                    for (ConsumerRecord<K, V> record : records) {
                        try {
                            handler.handle(record);
                        } catch (Exception e) {

                        }
                    }

                    commitIfNeeded();
                }
            }
        } catch (WakeupException we) {
            if (running.get()) throw we;
        } catch (Exception e) {

        } finally {
            safeCloseConsumer();
        }
    }

    public void stop() {
        if (!running.compareAndSet(true, false)) {
            return;
        }

        consumer.wakeup();

        Future<?> f = task;
        if (f != null) {
            try {
                f.get(3, TimeUnit.SECONDS);
            } catch (Exception ignore) {

            }
        }

        executor.shutdownNow();
    }

    public boolean isRunning() {
        return running.get();
    }

    private void commitIfNeeded() {
        switch (commitMode) {
            case AUTO -> {

            }
            case MANUAL_SYNC -> {
                try {
                    consumer.commitSync();
                } catch (Exception e) {

                }
            }
            case MANUAL_ASYNC -> {
                try {
                    consumer.commitAsync((offsets, ex) -> {
                    });
                } catch (Exception e) {

                }
            }
        }
    }

    private void safeCloseConsumer() {
        try {
            consumer.close();
        } catch (Exception ignore) {

        }
    }

    @Override
    public void close() throws Exception {
        stop();
    }
}
