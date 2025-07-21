package net.microfin.financeapp.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient( name = "transfer-service",
        fallback = TransferClientFallback.class)
public interface TransferClientImpl extends TransferClient {
}
