package net.microfin.financeapp.client;

import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.dto.NotificationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


@Slf4j
@Component
public class NotificationClientFallback implements NotificationClient {

    @Override
    public ResponseEntity<List<NotificationDTO>> listNotificationsByUserId(Integer userId) {
        log.warn("Fallback triggered for listNotificationsByUserId with userId={} due to service unavailability", userId);
        return ResponseEntity.ok(Collections.emptyList());
    }

    @Override
    public ResponseEntity<NotificationDTO> saveNotification(NotificationDTO dto) {
        return ResponseEntity.status(503).body(null);
    }
}
