package com.chat.producer.mapper;

import com.chat.model.MessageModel;
import com.chat.producer.kafka.MessageKafka;
import com.chat.request.message.CreateMessageRequest;
import com.chat.request.message.PatchMessageRequest;
import com.chat.request.message.UpdateMessageRequest;
import com.chat.response.message.MessageResponse;
import org.mapstruct.Mapper;

@Mapper
public interface MessageMapper {

    /* create */
    MessageModel createMessageRequestToMessageModel(CreateMessageRequest messageDto);
    CreateMessageRequest messageKafkaToCreateMessageRequest(MessageKafka messageKafka);
    MessageKafka createMessageRequestToMessageKafka(CreateMessageRequest createMessageRequest);

    /* update */
    MessageModel updateMessageRequestToMessageModel(UpdateMessageRequest messageDto);
    UpdateMessageRequest updateKafkaToCreateMessageRequest(MessageKafka messageKafka);
    MessageKafka updateMessageRequestToMessageKafka(UpdateMessageRequest updateMessageRequest);

    /* patch */
    MessageModel patchMessageRequestToMessageModel(PatchMessageRequest messageDto);
    PatchMessageRequest patchKafkaToCreateMessageRequest(MessageKafka messageKafka);
    MessageKafka patchMessageRequestToMessageKafka(PatchMessageRequest patchMessageRequest);

    MessageResponse messageModelToMessageResponse(MessageModel messageModel);

}
