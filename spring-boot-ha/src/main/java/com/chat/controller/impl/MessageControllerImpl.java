package com.chat.controller.impl;

import com.chat.controller.MessageController;
import com.chat.producer.mapper.MessageMapper;
import com.chat.request.message.CreateMessageRequest;
import com.chat.request.message.PatchMessageRequest;
import com.chat.request.message.UpdateMessageRequest;
import com.chat.response.message.MessageResponse;
import com.chat.service.MessageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
public class MessageControllerImpl implements MessageController {

    private final MessageService messageService;
    private final MessageMapper messageMapper;

    public MessageControllerImpl(@Qualifier("notification") MessageService messageService, MessageMapper messageMapper) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }

    @Override
    public ResponseEntity<MessageResponse> getMessage(UUID id) {
        return messageService.findById(id)
                .map(messageModel -> ResponseEntity.ok(messageMapper.messageModelToMessageResponse(messageModel)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> postMessage(CreateMessageRequest createMessageRequest) {
        UUID id = UUID.randomUUID();
        createMessageRequest.setId(id);
        messageService.createMessage(createMessageRequest);
        return ResponseEntity.accepted()
                .location(URI.create(String.format("/message/%s", id)))
                .build();
    }

    @Override
    public ResponseEntity<Void> putMessage(UUID id, UpdateMessageRequest updateMessageRequest) {
        updateMessageRequest.setId(id);
        messageService.updateMessage(updateMessageRequest);
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> patchMessage(UUID id, PatchMessageRequest patchMessageRequest) {
        patchMessageRequest.setId(id);
        messageService.patchMessage(patchMessageRequest);
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> deleteMessage(@NonNull UUID id) {
        messageService.deleteMessage(id);
        return ResponseEntity.accepted().build();
    }
}
