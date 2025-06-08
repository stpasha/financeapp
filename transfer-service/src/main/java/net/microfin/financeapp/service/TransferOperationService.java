package net.microfin.financeapp.service;

import net.microfin.financeapp.dto.TransferOperationDTO;
import net.microfin.financeapp.dto.TransferOperationResultDTO;
import org.springframework.http.ResponseEntity;

public interface TransferOperationService {
    ResponseEntity<TransferOperationResultDTO> performOperation(TransferOperationDTO transferOperationDTO);
}
