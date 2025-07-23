package net.microfin.financeapp.client;

import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.dto.ExchangeOperationDTO;
import net.microfin.financeapp.dto.ExchangeOperationResultDTO;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExchangeClientFallback implements ExchangeClientImpl {
    @Override
    public ResponseEntity<ExchangeOperationResultDTO> exchangeOperation(ExchangeOperationDTO dto) {
        log.warn("Fallback: exchangeOperation failed");
        return ResponseEntity.status(503).body(ExchangeOperationResultDTO.builder()
                .status(OperationStatus.FAILED)
                .message("Exchange server unavailable")
                .build());
    }
}
