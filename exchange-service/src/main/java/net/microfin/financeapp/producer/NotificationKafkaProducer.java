package net.microfin.financeapp.producer;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.NotificationDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationKafkaProducer {
    private final KafkaTemplate<UUID, NotificationDTO> kafkaTemplate;

    public void send(NotificationDTO notificationDTO) {
        kafkaTemplate.send("input-notification", notificationDTO.getUserId(), notificationDTO);
    }
}
