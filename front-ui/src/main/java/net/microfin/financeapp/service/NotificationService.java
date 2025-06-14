package net.microfin.financeapp.service;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.client.NotificationClient;
import net.microfin.financeapp.dto.NotificationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationClient notificationClient;

    public List<NotificationDTO> listNotifications(Integer userId) {
        ResponseEntity<List<NotificationDTO>> notificationsByUserId = notificationClient.listNotificationsByUserId(userId);
        if (notificationsByUserId.getStatusCode().is2xxSuccessful()) {
            return notificationsByUserId.getBody();
        }
        return List.of();
    }
}
