package com.chat.service;

import com.chat.model.MessageModel;
import com.chat.repository.MessageRepository;
import com.chat.request.message.CreateMessageRequest;
import com.chat.request.message.PatchMessageRequest;
import com.chat.request.message.UpdateMessageRequest;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractMessageService implements MessageService {

    protected final MessageRepository messageRepository;

    public AbstractMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Optional<MessageModel> findById(UUID id) {
        return messageRepository.findById(id);
    }
}
