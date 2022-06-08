package org.logs.model;

import org.json.simple.JSONObject;

import java.util.Optional;

public class FileLogEvent implements Event {

    public static final String JSON_ID = "id";
    public static final String JSON_STATE = "state";
    public static final String JSON_TYPE = "type";
    public static final String JSON_HOST = "host";
    public static final String JSON_TIMESTAMP = "timestamp";

    private String id;
    private String state;
    private long timestamp;
    private Optional<String> type;
    private Optional<String> host;


    public FileLogEvent(JSONObject jsonObject) {
        this.id = (String) jsonObject.get(JSON_ID);
        this.state = (String) jsonObject.get(JSON_STATE);
        this.timestamp = (long) jsonObject.get(JSON_TIMESTAMP);
        this.type = Optional.ofNullable((String) jsonObject.get(JSON_TYPE));
        this.host = Optional.ofNullable((String) jsonObject.get(JSON_HOST));
    }


    public String getId() {
        return id;
    }


    public String getState() {
        return state;
    }


    public long getTimestamp() {
        return timestamp;
    }


    public Optional<String> getType() {
        return type;
    }


    public Optional<String> getHost() {
        return host;
    }

    @Override
    public String getEventType() {
        return "EVENTLINE";
    }

    @Override
    public String toString() {
        return "FileLogEvent{" +
                "id='" + id + '\'' +
                ", state='" + state + '\'' +
                ", timestamp=" + timestamp +
                ", type=" + type +
                ", host=" + host +
                '}';
    }
}
