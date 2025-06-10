package net.microfin.financeapp.client;

import net.microfin.financeapp.dto.NotificationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "notification-client", url = "http://gateway-service:8082")
public interface NotificationClient {
    @GetMapping("/notification/user/{userId}")
    ResponseEntity<List<NotificationDTO>> listNotificationsByUserId(@PathVariable("userId") Integer userId);
}
