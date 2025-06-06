package net.microfin.financeapp.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.ExchangeOperationDTO;
import net.microfin.financeapp.dto.ExchangeOperationResultDTO;
import net.microfin.financeapp.service.ExchangeOperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
@PreAuthorize("hasRole('zbank.user')")
public class ExchangeOperationApi {

    private final ExchangeOperationService cashOperationService;

    @PostMapping("/operation")
    public ResponseEntity<ExchangeOperationResultDTO> performOperation(@Valid @RequestBody ExchangeOperationDTO exchangeOperationDTO) {
        return cashOperationService.performOperation(exchangeOperationDTO);
    }
}
