package net.microfin.financeapp.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.client.AccountClient;
import net.microfin.financeapp.client.CashClient;
import net.microfin.financeapp.client.ExchangeClient;
import net.microfin.financeapp.client.TransferClient;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.beans.factory.annotation.Value;
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
    private final CashClient cashClient;
    private final ExchangeClient exchangeClient;
    private final TransferClient transferClient;
    private final MeterRegistry meterRegistry;
    @Setter
    @Value("${testPrometeus}")
    private boolean testPrometeus;

    public OperationResult createCashOperation(CashOperationDTO dto) {
        ResponseEntity<CashOperationResultDTO> response = cashClient.cashOperation(dto);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        return response.getBody();
    }


    public OperationResult createExchangeOperation(ExchangeOperationDTO dto) {
        ResponseEntity<ExchangeOperationResultDTO> response = exchangeClient.exchangeOperation(dto);
        if (response.getStatusCode().is2xxSuccessful() && !OperationStatus.FAILED.equals(response.getBody().getStatus())) {
            return response.getBody();
        }
        if (testPrometeus) {
            meterRegistry.counter("financeapp_failed_transfers_total",
                    "userId", String.valueOf(dto.getUserId()),
                    "sourceAccountId", String.valueOf(dto.getSourceAccountId()),
                    "targetAccountId", String.valueOf(dto.getTargetAccountId())).increment();
        }
        return response.getBody();
    }

    public OperationResult createTransferOperation(TransferOperationDTO dto) {
        ResponseEntity<TransferOperationResultDTO> response = transferClient.transferOperation(dto);
        if (response.getStatusCode().is2xxSuccessful() && !OperationStatus.FAILED.equals(response.getBody().getStatus())) {
            return response.getBody();
        }
        if (testPrometeus) {
            meterRegistry.counter("financeapp_failed_transfers_total",
                    "userId", String.valueOf(dto.getUserId()),
                    "sourceAccountId", String.valueOf(dto.getSourceAccountId()),
                    "targetAccountId", String.valueOf(dto.getTargetAccountId())).increment();
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
