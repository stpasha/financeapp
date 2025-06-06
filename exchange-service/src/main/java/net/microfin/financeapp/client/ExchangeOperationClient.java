package net.microfin.financeapp.client;

import net.microfin.financeapp.config.FeignConfig;
import net.microfin.financeapp.dto.AccountDTO;
import net.microfin.financeapp.dto.CurrencyDTO;
import net.microfin.financeapp.dto.ExchangeOperationDTO;
import net.microfin.financeapp.dto.ExchangeOperationResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = "exchange-client", url = "http://gateway-service:8082",  configuration = FeignConfig.class)
public interface ExchangeOperationClient {

    @PostMapping("/account/operation")
    ResponseEntity<ExchangeOperationResultDTO> exchangeOperation(@RequestBody ExchangeOperationDTO exchangeOperationDTO);

    @GetMapping("/dictionary/currency")
    ResponseEntity<List<CurrencyDTO>> listCurrency();

    @GetMapping("/account/{id}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable("id") Integer id);
}
