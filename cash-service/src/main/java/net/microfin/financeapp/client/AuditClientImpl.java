package net.microfin.financeapp.client;

import net.microfin.financeapp.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "audit-service",
        configuration = FeignConfig.class,
        fallback = AuditClientFallBack.class
)
public interface AuditClientImpl extends AuditClient {
}
