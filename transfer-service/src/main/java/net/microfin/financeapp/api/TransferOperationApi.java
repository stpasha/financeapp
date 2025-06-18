package net.microfin.financeapp.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import net.microfin.financeapp.dto.TransferOperationDTO;

import net.microfin.financeapp.dto.TransferOperationResultDTO;
import net.microfin.financeapp.service.TransferOperationService;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferOperationApi {

    private final TransferOperationService transferOperationService;

    @PostMapping("/operation")
    public ResponseEntity<TransferOperationResultDTO> performOperation(@Valid @RequestBody TransferOperationDTO transferOperationDTO) {
        TransferOperationResultDTO resultDTO = transferOperationService.performOperation(transferOperationDTO);
        if (!OperationStatus.FAILED.equals(resultDTO.getStatus())) {
            return ResponseEntity.ok(resultDTO);
        }
        return ResponseEntity.badRequest().body(resultDTO);
    }
}
