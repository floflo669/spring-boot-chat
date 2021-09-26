package com.chat.request.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotEmpty;
import java.util.UUID;

public class CreateMessageRequest {

    @JsonIgnore
    private UUID id;

    @NotEmpty
    private String content;

    private UUID channelId;

    public CreateMessageRequest() {
        // nothing
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    @NonNull
    public UUID getChannelId() {
        return channelId;
    }

    public void setChannelId(@NonNull UUID channelId) {
        this.channelId = channelId;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
