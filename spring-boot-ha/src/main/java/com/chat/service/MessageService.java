package com.chat.service;

import com.chat.model.MessageModel;
import com.chat.request.message.CreateMessageRequest;
import com.chat.request.message.PatchMessageRequest;
import com.chat.request.message.UpdateMessageRequest;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface MessageService {

    Optional<MessageModel> findById(UUID id);

    void createMessage(CreateMessageRequest createMessageRequest);

    void updateMessage(UpdateMessageRequest updateMessageRequest);

    void patchMessage(PatchMessageRequest patchMessageRequest);

    void deleteMessage(@NonNull UUID id);

}
