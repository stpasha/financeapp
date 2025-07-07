package net.microfin.financeapp.client;

import net.microfin.financeapp.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "gateway-service",
        configuration = FeignConfig.class,
        fallback = GatewayClientFallback.class,
        url = "http://finance.local/"
)
public interface ExchangeOperationClient extends GatewayClient {
}
