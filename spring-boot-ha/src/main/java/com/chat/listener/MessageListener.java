package com.chat.listener;

import com.chat.producer.kafka.MessageKafka;
import com.chat.producer.kafka.Operation;
import com.chat.producer.mapper.MessageMapper;
import com.chat.service.MessageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MessageListener {

    private final MessageService messageService;

    private final MessageMapper messageMapper;

    public MessageListener(@Qualifier("data") MessageService messageService, MessageMapper messageMapper) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }

    @KafkaListener(topics = "${kafka.topic.message}", groupId = "chat-service")
    public void input(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key, @Payload(required = false) MessageKafka messageKafka,
                      @Header("operation") byte[] operationByte) {
        Operation operation = Operation.fromBytes(operationByte);
        if (operation != null) {
            switch (operation) {
                case ADD:
                    messageService.createMessage(messageMapper.messageKafkaToCreateMessageRequest(messageKafka));
                    break;
                case PATCH:
                    messageService.patchMessage(messageMapper.patchKafkaToCreateMessageRequest(messageKafka));
                    break;
                case REMOVE:
                    messageService.deleteMessage(UUID.fromString(key));
                    break;
                case UPDATE:
                    messageService.updateMessage(messageMapper.updateKafkaToCreateMessageRequest(messageKafka));
                    break;
            }
        }
    }
}
