package net.microfin.financeapp.client;

import net.microfin.financeapp.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "account-client", url = "http://gateway-service:8082")
public interface AccountClient {
    @PostMapping("/account")
    ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO);

    @GetMapping("/account/user/{userId}")
    ResponseEntity<List<AccountDTO>> getAccountsByUser(@PathVariable("userId") Integer userId);

    @PutMapping("/account/{id}/disable")
    ResponseEntity disable(@PathVariable("id") Integer id);

    @PostMapping("/cash/operation")
    ResponseEntity<CashOperationResultDTO> cashOperation(@RequestBody CashOperationDTO cashOperationDTO);

    @PostMapping("/exchange/operation")
    ResponseEntity<CashOperationResultDTO> exchangeOperation(@RequestBody ExchangeOperationDTO exchangeOperationDTO);

    @PostMapping("/transfer/operation")
    ResponseEntity<CashOperationResultDTO> transferOperation(@RequestBody TransferOperationDTO exchangeOperationDTO);


}
