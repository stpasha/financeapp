package net.microfin.financeapp.client;

import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.ExchangeOperationDTO;
import net.microfin.financeapp.dto.TransferOperationDTO;
import org.springframework.http.ResponseEntity;

public class AuditClientFallBack implements AuditClient {

    @Override
    public ResponseEntity<Boolean> check(CashOperationDTO cashOperationDTO) {
        return ResponseEntity.ok(false);
    }

    @Override
    public ResponseEntity<Boolean> check(TransferOperationDTO cashOperationDTO) {
        return ResponseEntity.ok(false);
    }

    @Override
    public ResponseEntity<Boolean> check(ExchangeOperationDTO cashOperationDTO) {
        return ResponseEntity.ok(false);
    }
}
