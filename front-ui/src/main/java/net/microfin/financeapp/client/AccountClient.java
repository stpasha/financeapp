package net.microfin.financeapp.client;

import net.microfin.financeapp.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@FeignClient( name = "account-client", url = "http://finance.local", fallback = AccountClientFallback.class)
public interface AccountClient {

    @GetMapping("/api/account/user/{userId}")
    ResponseEntity<List<AccountDTO>> getAccountsByUser(@PathVariable("userId") Integer userId);

    @PutMapping("/api/account/{id}/disable")
    ResponseEntity<Void> disable(@PathVariable("id") Integer id);

    @PostMapping("/api/cash/operation")
    ResponseEntity<CashOperationResultDTO> cashOperation(@RequestBody CashOperationDTO cashOperationDTO);

    @PostMapping("/api/exchange/operation")
    ResponseEntity<ExchangeOperationResultDTO> exchangeOperation(@RequestBody ExchangeOperationDTO exchangeOperationDTO);

    @PostMapping("/api/transfer/operation")
    ResponseEntity<TransferOperationResultDTO> transferOperation(@RequestBody TransferOperationDTO exchangeOperationDTO);
}
