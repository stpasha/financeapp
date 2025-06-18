package net.microfin.financeapp.client;

import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.dto.CurrencyDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class DictionaryClientFallback implements DictionaryClient {

    @Override
    public ResponseEntity<List<CurrencyDTO>> listCurrency() {
        log.warn("Fallback triggered for listCurrency due to service unavailability");
        return ResponseEntity.ok(Collections.emptyList());
    }
}
