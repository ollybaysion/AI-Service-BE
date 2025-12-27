package com.dateguide.stream.message;

public enum StreamEventType {
    ACK,
    DONE,
    ERROR,
    HEARTBEAT;

    public String sseEventName() {
        return name().toLowerCase();
    }

    public boolean isTerminal() {
        return this == DONE || this == ERROR;
    }
}
