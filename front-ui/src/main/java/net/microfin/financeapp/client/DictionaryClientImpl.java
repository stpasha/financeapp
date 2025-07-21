package net.microfin.financeapp.client;

import net.microfin.financeapp.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "dictionaries-service",
        configuration = FeignConfig.class,
        fallback = NotificationClientFallback.class
)
public interface DictionaryClientImpl extends DictionaryClient {
}
