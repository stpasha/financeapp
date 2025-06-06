package net.microfin.financeapp.service;

import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.CashOperationResultDTO;
import org.springframework.http.ResponseEntity;

public interface CashOperationService {
    ResponseEntity<CashOperationResultDTO> performOperation(CashOperationDTO operationDTO);
}
