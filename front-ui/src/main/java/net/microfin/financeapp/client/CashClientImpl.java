package net.microfin.financeapp.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "${client.service.cash}",
        fallback = CashClientFallback.class)
public interface CashClientImpl extends CashClient {
}
