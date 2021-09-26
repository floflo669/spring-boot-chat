package com.chat.producer.kafka;

import java.util.UUID;

public class MessageKafka {

    private UUID id;

    private String content;

    private UUID channelId;

    public MessageKafka() {
        // nothing
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void setChannelId(UUID channelId) {
        this.channelId = channelId;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
