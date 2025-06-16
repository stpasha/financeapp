package net.microfin.financeapp.client;

import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GatewayClientFallback implements GatewayClient {

    @Override
    public ResponseEntity<ExchangeOperationResultDTO> exchangeOperation(ExchangeOperationDTO dto) {
        return ResponseEntity.status(503).body(ExchangeOperationResultDTO.builder().message("Fallback: exchange failed").status(OperationStatus.FAILED).build());
    }

    @Override
    public ResponseEntity<CashOperationResultDTO> cashOperation(CashOperationDTO dto) {
        return ResponseEntity.status(503).body(CashOperationResultDTO.builder().message("Fallback: cash failed").status(OperationStatus.FAILED).build());
    }

    @Override
    public ResponseEntity<Boolean> check(CashOperationDTO cashOperationDTO) {
        return ResponseEntity.ok(false);
    }

    @Override
    public ResponseEntity<TransferOperationResultDTO> transferOperation(TransferOperationDTO dto) {
        return ResponseEntity.status(503).body(TransferOperationResultDTO.builder().message("Fallback: transfer failed").status(OperationStatus.FAILED).build());
    }

    @Override
    public ResponseEntity<Boolean> check(TransferOperationDTO cashOperationDTO) {
        return ResponseEntity.ok(false);
    }

    @Override
    public ResponseEntity<AccountDTO> getAccount(Integer id) {
        return ResponseEntity.status(503).body(null);
    }

    @Override
    public ResponseEntity<Boolean> check(ExchangeOperationDTO cashOperationDTO) {
        return ResponseEntity.ok(false);
    }

    @Override
    public ResponseEntity<List<CurrencyDTO>> listCurrency() {
        return ResponseEntity.status(503).body(List.of());
    }

    @Override
    public ResponseEntity<NotificationDTO> saveNotification(NotificationDTO dto) {
        return ResponseEntity.status(503).body(null);
    }

}
