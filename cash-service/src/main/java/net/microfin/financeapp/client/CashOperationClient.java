package net.microfin.financeapp.client;

import net.microfin.financeapp.config.FeignConfig;
import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.CashOperationResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "cash-client", url = "http://gateway-service:8082",  configuration = FeignConfig.class)
public interface CashOperationClient {

    @PostMapping("/account/operation")
    ResponseEntity<CashOperationResultDTO> cashOperation(@RequestBody CashOperationDTO cashOperationDTO);

    @GetMapping("/audit")
    ResponseEntity<Boolean> check(@RequestBody CashOperationDTO cashOperationDTO);
}
