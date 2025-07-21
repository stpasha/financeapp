package net.microfin.financeapp.client;

import net.microfin.financeapp.dto.CurrencyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public interface DictionaryClient {
    @GetMapping("/api/dictionary/currency")
    ResponseEntity<List<CurrencyDTO>> listCurrency();
}
