package net.microfin.financeapp.client;

import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.dto.TransferOperationDTO;
import net.microfin.financeapp.dto.TransferOperationResultDTO;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferClientFallback implements TransferClient {
    @Override
    public ResponseEntity<TransferOperationResultDTO> transferOperation(TransferOperationDTO dto) {
        log.warn("Fallback: transferOperation failed");
        return ResponseEntity.status(503).body(TransferOperationResultDTO.builder()
                .status(OperationStatus.FAILED)
                .message("Transfer service unavailable")
                .build());
    }
}
