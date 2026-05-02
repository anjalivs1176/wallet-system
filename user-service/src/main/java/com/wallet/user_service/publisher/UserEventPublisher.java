package com.wallet.user_service.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.wallet.user_service.event.UserCreatedEvent;

@Service
public class UserEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUserCreatedEvent(UserCreatedEvent event) {
        kafkaTemplate.send("user-created-topic", event);
    }
}
