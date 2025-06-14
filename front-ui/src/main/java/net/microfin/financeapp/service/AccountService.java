package net.microfin.financeapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.client.AccountClient;
import net.microfin.financeapp.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountClient accountClient;

    public OperationResult createCashOperation(CashOperationDTO dto) {
        ResponseEntity<CashOperationResultDTO> response = accountClient.cashOperation(dto);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        return response.getBody();
    }


    public OperationResult createExchangeOperation(ExchangeOperationDTO dto) {
        ResponseEntity<ExchangeOperationResultDTO> response = accountClient.exchangeOperation(dto);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        return response.getBody();
    }

    public OperationResult createTransferOperation(TransferOperationDTO dto) {
        ResponseEntity<TransferOperationResultDTO> response = accountClient.transferOperation(dto);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        return response.getBody();
    }

    public List<AccountDTO> getAccountsByUser(Integer userId) {
        ResponseEntity<List<AccountDTO>> response = accountClient.getAccountsByUser(userId);
        if (response.getStatusCode().is2xxSuccessful()) {
            return Optional.ofNullable(response.getBody()).orElse(Collections.emptyList());
        }
        return List.of();
    }

}
