package net.microfin.financeapp.service;

import net.microfin.financeapp.dto.ExchangeOperationDTO;
import net.microfin.financeapp.dto.ExchangeOperationResultDTO;
import org.springframework.http.ResponseEntity;

public interface ExchangeOperationService {
    ResponseEntity<ExchangeOperationResultDTO> performOperation(ExchangeOperationDTO exchangeOperationDTO);
}
