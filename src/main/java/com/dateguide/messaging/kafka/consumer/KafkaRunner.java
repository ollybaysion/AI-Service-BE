package com.dateguide.messaging.kafka.consumer;

import jakarta.annotation.PreDestroy;
import org.springframework.context.SmartLifecycle;

import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaRunner implements SmartLifecycle {

    private final KafkaPoller<?, ?> poller;
    private final AtomicBoolean running = new AtomicBoolean(false);

    private final int phase;

    private final boolean autoStartup;

    public KafkaRunner(KafkaPoller<?, ?> poller) {
        this(poller, 0, true);
    }

    public KafkaRunner(KafkaPoller<?, ?> poller, int phase, boolean autoStartup) {
        this.poller = poller;
        this.phase = phase;
        this.autoStartup = autoStartup;
    }

    @Override
    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        poller.start();
    }

    @Override
    public void stop() {
        if (!running.compareAndSet(true, false)) {
            return;
        }
        poller.stop();
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public boolean isAutoStartup() {
        return autoStartup;
    }

    @Override
    public int getPhase() {
        return phase;
    }


    @PreDestroy
    public void onDestroy() {
        try {
            stop();
        } catch (Exception ignore) {

        }
    }
}
