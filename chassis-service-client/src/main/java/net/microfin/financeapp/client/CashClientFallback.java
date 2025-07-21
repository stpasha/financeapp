package net.microfin.financeapp.client;

import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.CashOperationResultDTO;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CashClientFallback implements CashClient {

    @Override
    public ResponseEntity<CashOperationResultDTO> cashOperation(CashOperationDTO dto) {
        log.warn("Fallback: cashOperation failed");
        return ResponseEntity.status(503).body(CashOperationResultDTO.builder()
                .status(OperationStatus.FAILED)
                .message("Cash server unavailable")
                .build());
    }
}
