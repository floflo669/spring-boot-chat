package com.chat.producer;

import com.chat.producer.kafka.Operation;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

public abstract class GenericProducer<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;
    private final String topicName;

    public GenericProducer(String topicName, KafkaTemplate<String, T> kafkaTemplate) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(T object, String id, Operation operation) {
        ProducerRecord<String, T> record = new ProducerRecord<>(topicName, id, object);
        record.headers().add("operation", operation.name().getBytes());
        kafkaTemplate.send(record);
    }
}
