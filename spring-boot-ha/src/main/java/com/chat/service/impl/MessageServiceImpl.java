package com.chat.service.impl;

import com.chat.producer.mapper.MessageMapper;
import com.chat.repository.MessageRepository;
import com.chat.request.message.CreateMessageRequest;
import com.chat.request.message.PatchMessageRequest;
import com.chat.request.message.UpdateMessageRequest;
import com.chat.service.AbstractMessageService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service(value = "data")
public class MessageServiceImpl extends AbstractMessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public MessageServiceImpl(MessageRepository messageRepository, MessageMapper messageMapper) {
        super(messageRepository);
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }

    @Override
    public void createMessage(CreateMessageRequest createMessageRequest) {
        messageRepository.save(messageMapper.createMessageRequestToMessageModel(createMessageRequest));
    }

    @Transactional
    @Override
    public void updateMessage(UpdateMessageRequest updateMessageRequest) {
        this.findById(updateMessageRequest.getId()).ifPresent(messageModel ->
                messageRepository.save(messageMapper.updateMessageRequestToMessageModel(updateMessageRequest)));
    }

    @Override
    public void patchMessage(PatchMessageRequest patchMessageRequest) {
        //TODO à faire au propre sur du multi-objet
        this.findById(patchMessageRequest.getId()).ifPresent(messageModel ->
                messageRepository.save(messageMapper.patchMessageRequestToMessageModel(patchMessageRequest)));
    }

    @Override
    public void deleteMessage(@NonNull UUID id) {
        messageRepository.deleteById(id);
    }

}
