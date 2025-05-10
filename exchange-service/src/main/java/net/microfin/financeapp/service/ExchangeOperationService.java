package net.microfin.financeapp.service;

import net.microfin.financeapp.dto.ExchangeOperationDTO;
import net.microfin.financeapp.dto.ExchangeOperationResultDTO;

public interface ExchangeOperationService {
    ExchangeOperationResultDTO performOperation(ExchangeOperationDTO exchangeOperationDTO);
}
