package net.microfin.financeapp.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.dto.NotificationDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserNotificationConsumer {
    private final ConcurrentHashMap<UUID, Deque<NotificationDTO>> userNotifications = new ConcurrentHashMap<>();

    private static final int MAX_NOTIFICATIONS_PER_USER = 100;

    @KafkaListener(topics = "user-notification", groupId = "user-app-group")
    private void listen(ConsumerRecord<UUID, NotificationDTO> notification) {
        userNotifications.compute(notification.key(), (userId, notifications) -> {
            if (notifications == null) {
                notifications = new ConcurrentLinkedDeque<>();
            }
            if (notifications.size() > MAX_NOTIFICATIONS_PER_USER) {
                notifications.removeLast();
            }
            notifications.add(notification.value());
            return notifications;
        });
        log.debug("Notification stored for user {}: {}", notification.key(), notification.value().getNotificationDescription());
    }

    public List<NotificationDTO> consumeNotifications(UUID userId) {
        Deque<NotificationDTO> deque = userNotifications.remove(userId);
        if (deque == null) {
            return List.of();
        }
        return new ArrayList<>(deque);
    }
}
