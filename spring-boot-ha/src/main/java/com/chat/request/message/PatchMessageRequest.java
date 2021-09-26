package com.chat.request.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.lang.NonNull;

import java.util.UUID;

public class PatchMessageRequest {

    @JsonIgnore
    private UUID id;

    private String content;

    public PatchMessageRequest() {
        // nothing
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
