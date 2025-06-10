package net.microfin.financeapp.service;

import net.microfin.financeapp.dto.NotificationDTO;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    Optional<NotificationDTO> saveNotification(NotificationDTO notificationDTO);

    List<NotificationDTO> receiveNotification(Integer userId);
}
