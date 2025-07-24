package net.microfin.financeapp.client;

import net.microfin.financeapp.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "${client.service.audit}",
        configuration = FeignConfig.class,
        fallback = AuditClientFallBack.class
)
public interface AuditClientImpl extends AuditClient {
}
