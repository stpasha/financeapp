package net.microfin.financeapp.client;

import net.microfin.financeapp.dto.CurrencyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "dictionary-client", url = "http://gateway-service:8082")
public interface DictionaryClient {
    @GetMapping("/dictionary/currency")
    ResponseEntity<List<CurrencyDTO>> listCurrency();
}
