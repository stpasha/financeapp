package net.microfin.financeapp.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "${client.service.transfer}",
        fallback = TransferClientFallback.class)
public interface TransferClientImpl extends TransferClient {
}
