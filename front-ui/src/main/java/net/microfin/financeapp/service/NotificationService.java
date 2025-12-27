package net.microfin.financeapp.service;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.consumer.UserNotificationConsumer;
import net.microfin.financeapp.dto.NotificationDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
//    private final NotificationClient notificationClient;
    private final UserNotificationConsumer userNotificationConsumer;

    public List<NotificationDTO> listNotifications(UUID userId) {
        return userNotificationConsumer.consumeNotifications(userId);
    }
}
