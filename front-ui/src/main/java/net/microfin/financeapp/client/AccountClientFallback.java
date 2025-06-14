package net.microfin.financeapp.client;

import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AccountClientFallback implements AccountClient {

    @Override
    public ResponseEntity<List<AccountDTO>> getAccountsByUser(Integer userId) {
        log.warn("Fallback: getAccountsByUser failed for userId={}", userId);
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<Void> disable(Integer id) {
        log.warn("Fallback: disable failed for id={}", id);
        return ResponseEntity.status(503).build();
    }

    @Override
    public ResponseEntity<CashOperationResultDTO> cashOperation(CashOperationDTO dto) {
        log.warn("Fallback: cashOperation failed");
        return ResponseEntity.status(503).body(CashOperationResultDTO.builder()
                .status(OperationStatus.FAILED)
                .message("Cash server unavailable")
                .build());
    }

    @Override
    public ResponseEntity<ExchangeOperationResultDTO> exchangeOperation(ExchangeOperationDTO dto) {
        log.warn("Fallback: exchangeOperation failed");
        return ResponseEntity.status(503).body(ExchangeOperationResultDTO.builder()
                .status(OperationStatus.FAILED)
                .message("Exchange server unavailable")
                .build());
    }

    @Override
    public ResponseEntity<TransferOperationResultDTO> transferOperation(TransferOperationDTO dto) {
        log.warn("Fallback: transferOperation failed");
        return ResponseEntity.status(503).body(TransferOperationResultDTO.builder()
                .status(OperationStatus.FAILED)
                .message("Transfer service unavailable")
                .build());
    }
}
