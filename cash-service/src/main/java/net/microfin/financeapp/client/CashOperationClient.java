package net.microfin.financeapp.client;

import net.microfin.financeapp.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "gateway-service",
        url = "http://finance.local/",
        configuration = FeignConfig.class,
        fallback = GatewayClientFallback.class)
public interface CashOperationClient extends GatewayClient {
}
