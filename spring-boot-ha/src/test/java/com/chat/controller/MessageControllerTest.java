package com.chat.controller;

import com.chat.model.MessageModel;
import com.chat.producer.mapper.MessageMapper;
import com.chat.repository.MessageRepository;
import com.chat.request.message.CreateMessageRequest;
import com.chat.producer.kafka.MessageKafka;
import com.chat.request.message.PatchMessageRequest;
import com.chat.request.message.UpdateMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@EmbeddedKafka(partitions = 1, ports = 9092)
@AutoConfigureMockMvc
@SpringBootTest
class MessageControllerTest {

    private static final String CONTENT = "Content";
    private static final UUID CHANNEL = UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    ConsumerFactory<String, MessageKafka> consumerFactory;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Value("${kafka.topic.message}")
    private String topicName;

    private KafkaMessageListenerContainer<String, MessageKafka> container;
    private BlockingQueue<ConsumerRecord<String, MessageKafka>> records;

    @BeforeEach
    public void beforeEach() {
        ContainerProperties containerProperties =
                new ContainerProperties(topicName);

        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, MessageKafka>) record -> records.add(record));

        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @AfterEach
    public void afterEach() {
        container.stop();
        messageRepository.deleteAll();
    }


    @Test
    public void testGetMessageNotFound() throws Exception {
        mockMvc.perform(get("/message/{id}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetMessageOk() throws Exception {
        MessageModel messageModel = new MessageModel();
        messageModel.setId(UUID.randomUUID());
        messageModel.setChannelId(UUID.randomUUID());
        messageModel.setContent(CONTENT);
        messageRepository.save(messageModel);

         mockMvc.perform(get("/message/{id}", messageModel.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content()
         .string(objectMapper.writeValueAsString(messageMapper.messageModelToMessageResponse(messageModel))));
    }

    @Test
    public void testSendMessageOk() throws Exception {
        CreateMessageRequest createMessageRequest = new CreateMessageRequest();
        createMessageRequest.setContent(CONTENT);
        createMessageRequest.setChannelId(CHANNEL);

        MvcResult resultRequest = mockMvc.perform(post("/message")
                .content(objectMapper.writeValueAsString(createMessageRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted()).andReturn();

        String location = resultRequest.getResponse().getHeader("Location");
        assertThat(location).isNotNull();
        assertThat(location).isNotEmpty();
        String idMessage = location.split("/message/")[1];
        assertThat(idMessage).isNotNull();
        assertThat(idMessage).isNotEmpty();

        ConsumerRecord<String, MessageKafka> consumerRecord = records.poll(800, TimeUnit.SECONDS);
        assertThat(consumerRecord).isNotNull();

        MessageKafka result = consumerRecord.value();
        assertThat(result.getContent()).isEqualTo(createMessageRequest.getContent());
        assertThat(result.getChannelId()).isEqualTo(createMessageRequest.getChannelId());
    }

    @Test
    public void testSendMessageNotValid() throws Exception {
        CreateMessageRequest createMessageRequest = new CreateMessageRequest();

        mockMvc.perform(post("/message")
                .content(objectMapper.writeValueAsString(createMessageRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPutMessageNotValid() throws Exception {
        UpdateMessageRequest updateMessageRequest = new UpdateMessageRequest();
        updateMessageRequest.setId(UUID.randomUUID());

        mockMvc.perform(put("/message/{id}", updateMessageRequest.getId())
                .content(objectMapper.writeValueAsString(updateMessageRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPutMessageOk() throws Exception {
        UpdateMessageRequest updateMessageRequest = new UpdateMessageRequest();
        updateMessageRequest.setId(UUID.randomUUID());
        updateMessageRequest.setContent(CONTENT);

        mockMvc.perform(put("/message/{id}", updateMessageRequest.getId())
                .content(objectMapper.writeValueAsString(updateMessageRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

        ConsumerRecord<String, MessageKafka> consumerRecord = records.poll(800, TimeUnit.SECONDS);
        assertThat(consumerRecord).isNotNull();

        MessageKafka result = consumerRecord.value();
        assertThat(result.getContent()).isEqualTo(updateMessageRequest.getContent());
    }

    @Test
    public void testPatchMessageNotValid() throws Exception {
        PatchMessageRequest patchMessageRequest = new PatchMessageRequest();
        patchMessageRequest.setId(UUID.randomUUID());

        mockMvc.perform(put("/message/{id}", patchMessageRequest.getId())
                .content(objectMapper.writeValueAsString(patchMessageRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPatchMessageOk() throws Exception {
        PatchMessageRequest patchMessageRequest = new PatchMessageRequest();
        patchMessageRequest.setId(UUID.randomUUID());
        patchMessageRequest.setContent(CONTENT);

        mockMvc.perform(patch("/message/{id}", patchMessageRequest.getId())
                .content(objectMapper.writeValueAsString(patchMessageRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());


        ConsumerRecord<String, MessageKafka> consumerRecord = records.poll(800, TimeUnit.SECONDS);
        assertThat(consumerRecord).isNotNull();

        MessageKafka result = consumerRecord.value();
        assertThat(result.getContent()).isEqualTo(patchMessageRequest.getContent());
    }

    @Test
    public void testDeleteMessageOk() throws Exception {
        MessageModel messageModel = new MessageModel();
        messageModel.setId(UUID.randomUUID());
        messageModel.setChannelId(UUID.randomUUID());
        messageModel.setContent(CONTENT);
        messageRepository.save(messageModel);

        mockMvc.perform(delete("/message/{id}", messageModel.getId()))
                .andExpect(status().isAccepted());


        ConsumerRecord<String, MessageKafka> consumerRecord = records.poll(800, TimeUnit.SECONDS);
        assertThat(consumerRecord).isNotNull();
    }
}