package net.microfin.financeapp.client;

import net.microfin.financeapp.dto.TransferOperationDTO;
import net.microfin.financeapp.dto.TransferOperationResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient( name = "transfer-client", url = "http://finance.local", fallback = TransferClientFallback.class)
public interface TransferClient {
    @PostMapping("/api/transfer/operation")
    ResponseEntity<TransferOperationResultDTO> transferOperation(@RequestBody TransferOperationDTO exchangeOperationDTO);
}
