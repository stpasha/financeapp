package net.microfin.financeapp.client;

import net.microfin.financeapp.dto.NotificationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "notification-client", url = "http://finance.local", fallback = NotificationClientFallback.class)
public interface NotificationClient {
    @GetMapping("/api/notification/user/{userId}")
    ResponseEntity<List<NotificationDTO>> listNotificationsByUserId(@PathVariable("userId") Integer userId);
}
