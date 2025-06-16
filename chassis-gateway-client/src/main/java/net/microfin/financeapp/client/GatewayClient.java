package net.microfin.financeapp.client;

import net.microfin.financeapp.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface GatewayClient {

    @PostMapping("/account/operation")
    ResponseEntity<ExchangeOperationResultDTO> exchangeOperation(@RequestBody ExchangeOperationDTO exchangeOperationDTO);

    @GetMapping("/dictionary/currency")
    ResponseEntity<List<CurrencyDTO>> listCurrency();

    @GetMapping("/account/{id}")
    ResponseEntity<AccountDTO> getAccount(@PathVariable("id") Integer id);

    @GetMapping("/audit")
    ResponseEntity<Boolean> check(@RequestBody ExchangeOperationDTO cashOperationDTO);

    @PostMapping("/notification")
    ResponseEntity<NotificationDTO> saveNotification(@RequestBody NotificationDTO notificationDTO);

    @PostMapping("/account/operation")
    ResponseEntity<CashOperationResultDTO> cashOperation(@RequestBody CashOperationDTO cashOperationDTO);

    @GetMapping("/audit")
    ResponseEntity<Boolean> check(@RequestBody CashOperationDTO cashOperationDTO);

    @PostMapping("/account/operation")
    ResponseEntity<TransferOperationResultDTO> transferOperation(@RequestBody TransferOperationDTO transferOperationDTO);

    @GetMapping("/audit")
    ResponseEntity<Boolean> check(@RequestBody TransferOperationDTO cashOperationDTO);

}
