package com.chat.producer;

import com.chat.producer.kafka.MessageKafka;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer extends GenericProducer<MessageKafka> {

    public MessageProducer(@Value("${kafka.topic.message}") String topicName, KafkaTemplate<String, MessageKafka> kafkaTemplate) {
        super(topicName, kafkaTemplate);
    }
}
