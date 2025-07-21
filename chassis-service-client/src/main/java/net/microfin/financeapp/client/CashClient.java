package net.microfin.financeapp.client;

import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.CashOperationResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface CashClient {
    @PostMapping("/api/cash/operation")
    ResponseEntity<CashOperationResultDTO> cashOperation(@RequestBody CashOperationDTO cashOperationDTO);
}
