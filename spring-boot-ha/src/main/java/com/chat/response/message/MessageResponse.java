package com.chat.response.message;

import java.time.ZonedDateTime;
import java.util.UUID;

public class MessageResponse {

    private String content;

    private UUID channelId;

    private ZonedDateTime creationDateTime;

    private ZonedDateTime lastModificationDateTime;

    public MessageResponse() {
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

    public ZonedDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(ZonedDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public ZonedDateTime getLastModificationDateTime() {
        return lastModificationDateTime;
    }

    public void setLastModificationDateTime(ZonedDateTime lastModificationDateTime) {
        this.lastModificationDateTime = lastModificationDateTime;
    }
}
