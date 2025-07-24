package net.microfin.financeapp.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "${client.service.exchange}",
        fallback = ExchangeClientFallback.class)
public interface ExchangeClientImpl extends ExchangeClient {
}
