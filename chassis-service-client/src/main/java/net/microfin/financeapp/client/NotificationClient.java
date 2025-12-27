package net.microfin.financeapp.client;

import net.microfin.financeapp.dto.NotificationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

public interface NotificationClient {
    @GetMapping("/api/notification/user/{userId}")
    ResponseEntity<List<NotificationDTO>> listNotificationsByUserId(@PathVariable("userId") UUID userId);

    @Deprecated
    @PostMapping("/api/notification")
    ResponseEntity<NotificationDTO> saveNotification(@RequestBody NotificationDTO notificationDTO);
}
