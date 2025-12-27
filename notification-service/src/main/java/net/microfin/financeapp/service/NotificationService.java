package net.microfin.financeapp.service;

import net.microfin.financeapp.dto.NotificationDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationService {
    Optional<NotificationDTO> saveNotification(NotificationDTO notificationDTO);

    List<NotificationDTO> receiveNotification(UUID userId);
}
