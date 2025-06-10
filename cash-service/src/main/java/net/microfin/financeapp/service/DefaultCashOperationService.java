package net.microfin.financeapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.client.CashOperationClient;
import net.microfin.financeapp.domain.CashOperation;
import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.CashOperationResultDTO;
import net.microfin.financeapp.dto.NotificationDTO;
import net.microfin.financeapp.mapper.CashOperationMapper;
import net.microfin.financeapp.repository.CashOperationRepository;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultCashOperationService implements CashOperationService {

    private final CashOperationClient cashOperationClient;
    private final CashOperationMapper cashOperationMapper;
    private final CashOperationRepository cashOperationRepository;

    @Override
    @Transactional
    public ResponseEntity<CashOperationResultDTO> performOperation(CashOperationDTO operationDTO) {
        ResponseEntity<Boolean> check = cashOperationClient.check(operationDTO);
        if (check.getStatusCode().is2xxSuccessful()) {
            if (Boolean.TRUE.equals(check.getBody())) {
                CashOperation cashOperation = cashOperationRepository.save(cashOperationMapper.toEntity(operationDTO));
                operationDTO.setId(cashOperation.getId());
                ResponseEntity<CashOperationResultDTO> cashOperationResultDTOResponseEntity = cashOperationClient.cashOperation(operationDTO);
                if (cashOperationResultDTOResponseEntity.getStatusCode().is2xxSuccessful()) {
                    cashOperationClient.saveNotification(NotificationDTO.builder()
                            .notificationDescription("Выполнена запрос на " + operationDTO.getOperationType() + " " +
                                    operationDTO.getAmount() + " " +
                                    operationDTO.getCurrencyCode().getName())
                                    .userId(operationDTO.getUserId())
                            .operationType(operationDTO.getOperationType().name()).build());
                }
                return cashOperationResultDTOResponseEntity;
            }
            return ResponseEntity.ok(CashOperationResultDTO.builder().message("Operation " + operationDTO.getOperationType() + " "
                    + operationDTO.getAmount() + " "+ "prohibitted").status(OperationStatus.FAILED.name()).build());
        }
        return ResponseEntity.ok(CashOperationResultDTO.builder().message("Server unavailable.").status(OperationStatus.FAILED.name()).build());
    }
}
