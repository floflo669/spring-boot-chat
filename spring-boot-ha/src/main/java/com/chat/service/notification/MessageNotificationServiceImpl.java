package com.chat.service.notification;

import com.chat.model.MessageModel;
import com.chat.producer.MessageProducer;
import com.chat.producer.kafka.MessageKafka;
import com.chat.producer.kafka.Operation;
import com.chat.producer.mapper.MessageMapper;
import com.chat.repository.MessageRepository;
import com.chat.request.message.CreateMessageRequest;
import com.chat.request.message.PatchMessageRequest;
import com.chat.request.message.UpdateMessageRequest;
import com.chat.service.AbstractMessageService;
import com.chat.service.MessageService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service(value = "notification")
public class MessageNotificationServiceImpl extends AbstractMessageService {

    private final MessageProducer messageProducer;
    private final MessageMapper messageMapper;

    public MessageNotificationServiceImpl(MessageProducer messageProducer, MessageMapper messageMapper, MessageRepository messageRepository) {
        super(messageRepository);
        this.messageProducer = messageProducer;
        this.messageMapper = messageMapper;
    }

    @Override
    public Optional<MessageModel> findById(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public void createMessage(CreateMessageRequest createMessageRequest) {
        messageProducer.sendMessage(
                messageMapper.createMessageRequestToMessageKafka(createMessageRequest),
                UUID.randomUUID().toString(),
                Operation.ADD
        );
    }

    @Override
    public void updateMessage(UpdateMessageRequest updateMessageRequest) {
        messageProducer.sendMessage(
                messageMapper.updateMessageRequestToMessageKafka(updateMessageRequest),
                UUID.randomUUID().toString(),
                Operation.UPDATE
        );
    }

    @Override
    public void patchMessage(PatchMessageRequest patchMessageRequest) {
        messageProducer.sendMessage(
                messageMapper.patchMessageRequestToMessageKafka(patchMessageRequest),
                UUID.randomUUID().toString(),
                Operation.PATCH
        );
    }

    @Override
    public void deleteMessage(@NonNull UUID id) {
        messageProducer.sendMessage(null, id.toString(), Operation.REMOVE);
    }
}
