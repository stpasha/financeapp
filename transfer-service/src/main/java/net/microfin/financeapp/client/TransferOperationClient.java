package net.microfin.financeapp.client;

import net.microfin.financeapp.config.FeignConfig;
import net.microfin.financeapp.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = "transfer-client", url = "http://gateway-service:8082",  configuration = FeignConfig.class)
public interface TransferOperationClient {

    @PostMapping("/account/operation")
    ResponseEntity<TransferOperationResultDTO> transferOperation(@RequestBody TransferOperationDTO transferOperationDTO);

    @GetMapping("/dictionary/currency")
    ResponseEntity<List<CurrencyDTO>> listCurrency();

    @GetMapping("/account/{id}")
    ResponseEntity<AccountDTO> getAccount(@PathVariable("id") Integer id);

    @GetMapping("/audit")
    ResponseEntity<Boolean> check(@RequestBody TransferOperationDTO cashOperationDTO);

    @PostMapping("/notification")
    ResponseEntity<NotificationDTO> saveNotification(@RequestBody NotificationDTO notificationDTO);
}
