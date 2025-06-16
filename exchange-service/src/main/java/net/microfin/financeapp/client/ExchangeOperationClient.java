package net.microfin.financeapp.client;

import net.microfin.financeapp.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "gateway-service", configuration = FeignConfig.class, fallback = GatewayClientFallback.class)
public interface ExchangeOperationClient extends GatewayClient {
}
