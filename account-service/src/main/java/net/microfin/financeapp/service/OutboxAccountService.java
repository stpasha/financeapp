package net.microfin.financeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.domain.OutboxEvent;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.repository.OutboxEventRepository;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OutboxAccountService {

    private final OutboxEventRepository eventRepository;
    private final ObjectMapper objectMapper;


    @Transactional
    public Optional<OperationResult> processOperation(GenericOperationDTO operationDTO) {
        Integer accountId = null;
        OperationResult result;
        result = TransferOperationResultDTO.builder()
                .operationId(operationDTO.getId())
                .message("Operation " +operationDTO.getOperationType().name()+ " delivered")
                .status(OperationStatus.PENDING)
                .build();

        saveOutboxEvent(accountId, operationDTO);

        return Optional.of(result);
    }


    private void saveOutboxEvent(Integer accountId, GenericOperationDTO operationDTO) {
        try {
            OutboxEvent event = OutboxEvent.builder()
                    .aggregateId(operationDTO.getId())
                    .accountId(accountId)
                    .aggregateType("ACCOUNT")
                    .operationType(operationDTO.getOperationType())
                    .payload(objectMapper.writeValueAsString(operationDTO))
                    .build();
            eventRepository.save(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize payload", e);
        }
    }

}
