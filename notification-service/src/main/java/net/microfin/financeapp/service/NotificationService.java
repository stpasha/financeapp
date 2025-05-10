package net.microfin.financeapp.service;

import net.microfin.financeapp.dto.NotificationDTO;

public interface NotificationService {
    NotificationDTO performNotification(NotificationDTO notificationDTO);
}
