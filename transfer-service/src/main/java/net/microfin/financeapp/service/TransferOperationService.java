package net.microfin.financeapp.service;

import net.microfin.financeapp.dto.TransferOperationDTO;
import net.microfin.financeapp.dto.TransferOperationResultDTO;

public interface TransferOperationService {
    TransferOperationResultDTO performOperation(TransferOperationDTO transferOperationDTO);
}
