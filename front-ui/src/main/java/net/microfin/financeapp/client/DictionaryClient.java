package net.microfin.financeapp.client;

import net.microfin.financeapp.config.FeignConfig;
import net.microfin.financeapp.dto.CurrencyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "external-api", url = "http://gateway-service:8082", configuration = FeignConfig.class)
public interface DictionaryClient {
    @GetMapping("/dictionaries/currency")
    ResponseEntity<List<CurrencyDTO>> listCurrencyList();
}
