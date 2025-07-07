package net.microfin.financeapp.client;

import net.microfin.financeapp.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "rule-client",
        url = "http://finance.local/",
        configuration = FeignConfig.class,
        fallback = GatewayClientFallback.class
)
public interface RuleClient extends GatewayClient {
}
