package org.logs.model;

public class EOFEvent implements Event {
    @Override
    public String getEventType() {
        return "EOF";
    }

    @Override
    public String toString() {
        return "EOFEvent{}";
    }
}
