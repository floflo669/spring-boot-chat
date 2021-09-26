package com.chat.listener;

import com.chat.model.MessageModel;
import com.chat.producer.kafka.MessageKafka;
import com.chat.producer.kafka.Operation;
import com.chat.producer.mapper.MessageMapper;
import com.chat.repository.MessageRepository;
import com.chat.request.message.PatchMessageRequest;
import com.chat.request.message.UpdateMessageRequest;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@EmbeddedKafka(partitions = 1, ports = 9091)
@SpringBootTest
class MessageListenerTest {

    private static final String CONTENT = "CONTENT";

    @Autowired
    private ProducerFactory<String, MessageKafka> producerFactory;

    @Autowired
    ConsumerFactory<String, MessageKafka> consumerFactory;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private MessageMapper messageMapper;

    @MockBean
    private MessageRepository messageRepository;

    @Captor
    private ArgumentCaptor<MessageModel> modelArgumentCaptor;

    @Value("${kafka.topic.message}")
    private String topicName;

    private KafkaMessageListenerContainer<String, MessageKafka> container;
    private KafkaTemplate<String, MessageKafka> template;
    private CountDownLatch latch;

    @BeforeEach
    public void setUp() {
        ContainerProperties containerProperties =
                new ContainerProperties(topicName);

        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

        template = new KafkaTemplate<>(producerFactory);
        template.setDefaultTopic(topicName);
        latch = new CountDownLatch(1);

        container.setupMessageListener((MessageListener<String, MessageKafka>) message -> {
            latch.countDown();
        });
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @AfterEach
    public void afterEach() {
        container.stop();
    }

    @Test
    public void sendPostMessageKafka() throws InterruptedException {
        MessageKafka messageKafka = new MessageKafka();
        messageKafka.setId(UUID.randomUUID());
        messageKafka.setChannelId(UUID.randomUUID());
        messageKafka.setContent(CONTENT);

        ProducerRecord<String, MessageKafka> producerRecord = new ProducerRecord<>(topicName, messageKafka.getId().toString(), messageKafka );
        producerRecord.headers().add("operation", Operation.ADD.name().getBytes());

        template.send(producerRecord);
        template.flush();
        assertThat(latch.await(800, TimeUnit.MILLISECONDS)).isTrue();

        Mockito.verify(messageRepository, Mockito.timeout(500).times(1)).save(modelArgumentCaptor.capture());

        assertThat(modelArgumentCaptor.getValue().getChannelId()).isEqualTo(messageKafka.getChannelId());
        assertThat(modelArgumentCaptor.getValue().getContent()).isEqualTo(messageKafka.getContent());
        assertThat(modelArgumentCaptor.getValue().getId()).isEqualTo(messageKafka.getId());
    }

    @Test
    public void sendUpdateMessageKafka() throws InterruptedException {
        MessageKafka messageKafka = new MessageKafka();
        messageKafka.setId(UUID.randomUUID());
        messageKafka.setChannelId(UUID.randomUUID());
        messageKafka.setContent(CONTENT);

        UpdateMessageRequest updateMessageRequest = messageMapper.updateKafkaToCreateMessageRequest(messageKafka);

        ProducerRecord<String, MessageKafka> producerRecord = new ProducerRecord<>(topicName, messageKafka.getId().toString(), messageKafka );
        producerRecord.headers().add("operation", Operation.UPDATE.name().getBytes());
        Mockito.when(messageRepository.findById(messageKafka.getId()))
                .thenReturn(Optional.of(messageMapper.updateMessageRequestToMessageModel(updateMessageRequest)));

        template.send(producerRecord);
        template.flush();
        assertThat(latch.await(800, TimeUnit.MILLISECONDS)).isTrue();

        Mockito.verify(messageRepository, Mockito.timeout(500).times(1)).save(modelArgumentCaptor.capture());

        assertThat(modelArgumentCaptor.getValue().getContent()).isEqualTo(messageKafka.getContent());
        assertThat(modelArgumentCaptor.getValue().getId()).isEqualTo(messageKafka.getId());
    }

    @Test
    public void sendPatchMessageKafka() throws InterruptedException {
        MessageKafka messageKafka = new MessageKafka();
        messageKafka.setId(UUID.randomUUID());
        messageKafka.setChannelId(UUID.randomUUID());
        messageKafka.setContent(CONTENT);

        PatchMessageRequest patchMessageRequest = messageMapper.patchKafkaToCreateMessageRequest(messageKafka);

        ProducerRecord<String, MessageKafka> producerRecord = new ProducerRecord<>(topicName, messageKafka.getId().toString(), messageKafka );
        producerRecord.headers().add("operation", Operation.UPDATE.name().getBytes());
        Mockito.when(messageRepository.findById(messageKafka.getId()))
                .thenReturn(Optional.of(messageMapper.patchMessageRequestToMessageModel(patchMessageRequest)));

        template.send(producerRecord);
        template.flush();
        assertThat(latch.await(800, TimeUnit.MILLISECONDS)).isTrue();

        Mockito.verify(messageRepository, Mockito.timeout(500).times(1)).save(modelArgumentCaptor.capture());

        assertThat(modelArgumentCaptor.getValue().getContent()).isEqualTo(messageKafka.getContent());
        assertThat(modelArgumentCaptor.getValue().getId()).isEqualTo(messageKafka.getId());
    }

    @Test
    public void sendDeleteMessageKafka() throws InterruptedException {
        UUID id = UUID.randomUUID();

        ProducerRecord<String, MessageKafka> producerRecord = new ProducerRecord<>(topicName, id.toString(), null );
        producerRecord.headers().add("operation", Operation.REMOVE.name().getBytes());

        template.send(producerRecord);
        template.flush();
        assertThat(latch.await(800, TimeUnit.MILLISECONDS)).isTrue();

        Mockito.verify(messageRepository, Mockito.timeout(500).times(1)).deleteById(Mockito.eq(id));

    }
}