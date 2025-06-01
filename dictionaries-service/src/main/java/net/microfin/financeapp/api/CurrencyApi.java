package net.microfin.financeapp.api;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.CurrencyDTO;
import net.microfin.financeapp.service.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dictionary")
@PreAuthorize("hasRole('zbank.user')")
public class CurrencyApi {

    private final CurrencyService currencyService;

    @GetMapping("/currency")
    public ResponseEntity<List<CurrencyDTO>> listCurrencyList() {
        return ResponseEntity.ok(currencyService.listCurrencyList());
    }
}
