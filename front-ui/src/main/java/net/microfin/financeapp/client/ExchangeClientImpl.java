package net.microfin.financeapp.client;

import net.microfin.financeapp.dto.ExchangeOperationDTO;
import net.microfin.financeapp.dto.ExchangeOperationResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient( name = "exchange-service",
        fallback = ExchangeClientFallback.class)
public interface ExchangeClientImpl extends ExchangeClient {
}
