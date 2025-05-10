package net.microfin.financeapp.service;

import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.CashOperationResultDTO;

public interface CashOperationService {
    CashOperationResultDTO performOperation(CashOperationDTO operationDTO);
}
