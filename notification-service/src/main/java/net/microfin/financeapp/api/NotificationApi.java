package net.microfin.financeapp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.NotificationDTO;
import net.microfin.financeapp.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@PreAuthorize("hasRole('zbank.user')")
public class NotificationApi {
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<NotificationDTO> save(@RequestBody NotificationDTO notificationDTO) {
        return notificationService.saveNotification(notificationDTO)
                .map(notif -> ResponseEntity.ok(notificationDTO))
                .orElse(ResponseEntity.badRequest().body(notificationDTO));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> listNotificationsByUserId(@PathVariable("userId") Integer userId) {
        return ResponseEntity.ok(notificationService.receiveNotification(userId));
    }

}
