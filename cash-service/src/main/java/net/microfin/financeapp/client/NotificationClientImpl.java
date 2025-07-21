package net.microfin.financeapp.client;

import net.microfin.financeapp.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "notification-service",
        configuration = FeignConfig.class,
        fallback = NotificationClientFallback.class
)
public interface NotificationClientImpl extends NotificationClient {
}
