package net.microfin.financeapp.service;

import net.microfin.financeapp.dto.TransferOperationDTO;

public interface TransferOperationService {
    TransferOperationDTO performNotification(TransferOperationDTO transferOperationDTO);
}
