package net.microfin.financeapp.service;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.consumer.UserNotificationConsumer;
import net.microfin.financeapp.dto.NotificationDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
//    private final NotificationClient notificationClient;
    private final UserNotificationConsumer userNotificationConsumer;

    public List<NotificationDTO> listNotifications(Integer userId) {
        return userNotificationConsumer.consumeNotifications(userId);
    }
}
