package com.chat.request.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotEmpty;
import java.util.UUID;

public class UpdateMessageRequest {

    @JsonIgnore
    private UUID id;

    @NotEmpty
    private String content;

    public UpdateMessageRequest() {
        // nothing
    }

    @NonNull
    public UUID getId() {
        return id;
    }

    public void setId(@NonNull UUID id) {
        this.id = id;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

}
