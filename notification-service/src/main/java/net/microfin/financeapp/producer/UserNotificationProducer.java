package net.microfin.financeapp.producer;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.NotificationDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserNotificationProducer {
    private final KafkaTemplate<Integer, NotificationDTO> kafkaTemplate;

    public void produceUserNotification(NotificationDTO notificationDTO) {
        kafkaTemplate.send("user-notification", notificationDTO.getUserId(), notificationDTO);
    }
}
