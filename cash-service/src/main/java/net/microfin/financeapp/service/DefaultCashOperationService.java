package net.microfin.financeapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.client.AccountClientImpl;
import net.microfin.financeapp.client.AuditClientImpl;
import net.microfin.financeapp.domain.CashOperation;
import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.CashOperationResultDTO;
import net.microfin.financeapp.dto.NotificationDTO;
import net.microfin.financeapp.mapper.CashOperationMapper;
import net.microfin.financeapp.producer.NotificationKafkaProducer;
import net.microfin.financeapp.repository.CashOperationRepository;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultCashOperationService implements CashOperationService {

    private final AuditClientImpl auditClient;
    private final NotificationKafkaProducer notificationKafkaProducer;
    private final AccountClientImpl accountClient;
    private final CashOperationMapper cashOperationMapper;
    private final CashOperationRepository cashOperationRepository;

    @Override
    @Transactional
    // TODO: replace afterCommit with Transactional Outbox / Saga
    public ResponseEntity<CashOperationResultDTO> performOperation(CashOperationDTO operationDTO) {
        ResponseEntity<Boolean> check = auditClient.check(operationDTO);
        if (check.getStatusCode().is2xxSuccessful()) {
            if (Boolean.TRUE.equals(check.getBody())) {
                CashOperation cashOperation = cashOperationRepository.save(cashOperationMapper.toEntity(operationDTO));
                if (TransactionSynchronizationManager.isSynchronizationActive()) {
                    TransactionSynchronizationManager.registerSynchronization(
                            new TransactionSynchronization() {
                                @Override
                                public void afterCommit() {
                                    try {
                                        processOperation(cashOperation);
                                    } catch (Exception e) {
                                        log.error(
                                                "Post-commit processing failed. operationId={}, userId={}, type={}",
                                                cashOperation.getId(),
                                                operationDTO.getUserId(),
                                                operationDTO.getOperationType(),
                                                e
                                        );
                                    }
                                }
                            }
                    );
                } else {
                    log.error("No active transaction. Post-commit actions skipped. operationId={}", cashOperation.getId());
                }
                return ResponseEntity.ok(CashOperationResultDTO.builder().message("Operation " + operationDTO.getOperationType() + " "
                        + operationDTO.getAmount() + " " + "sent").status(OperationStatus.PENDING).build());

            }
            return ResponseEntity.ok(CashOperationResultDTO.builder().message("Operation " + operationDTO.getOperationType() + " "
                    + operationDTO.getAmount() + " " + "prohibitted").status(OperationStatus.FAILED).build());
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(CashOperationResultDTO.builder().message("Server unavailable.").status(OperationStatus.FAILED).build());
    }

    private void processOperation(CashOperation cashOperation) {
        CashOperationDTO cashOperationDTO = CashOperationDTO.builder()
                .id(cashOperation.getId())
                .userId(cashOperation.getUserId())
                .operationType(cashOperation.getOperationType())
                .accountId(cashOperation.getAccountId())
                .amount(cashOperation.getAmount())
                .createdAt(cashOperation.getCreatedAt())
                .currencyCode(cashOperation.getCurrencyCode())
                .status(cashOperation.getStatus())
                .build();
        ResponseEntity<CashOperationResultDTO> cashOperationResultDTOResponseEntity = accountClient.cashOperation(cashOperationDTO);
        if (cashOperationResultDTOResponseEntity.getStatusCode().is2xxSuccessful()) {
            notificationKafkaProducer.send(NotificationDTO.builder()
                    .notificationDescription("Выполнен запрос на " + cashOperationDTO.getOperationType() + " " +
                            cashOperationDTO.getAmount() + " " +
                            cashOperationDTO.getCurrencyCode().getName())
                    .userId(cashOperationDTO.getUserId())
                    .operationType(cashOperationDTO.getOperationType().name()).build());
        }
    }
}
