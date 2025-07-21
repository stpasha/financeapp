package net.microfin.financeapp.client;

import net.microfin.financeapp.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "account-service",
        configuration = FeignConfig.class,
        fallback = AccountClientFallback.class
)
public interface AccountClientImpl extends AccountClient {
}
