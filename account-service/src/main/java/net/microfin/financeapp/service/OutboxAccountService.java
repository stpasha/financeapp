package net.microfin.financeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.GenericOperationDTO;
import net.microfin.financeapp.dto.OperationResult;
import net.microfin.financeapp.dto.TransferOperationResultDTO;
import net.microfin.financeapp.jooq.tables.records.OutboxEventsRecord;
import net.microfin.financeapp.repository.OutboxEventWriteRepository;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxAccountService {

    private final OutboxEventWriteRepository eventRepository;
    private final ObjectMapper objectMapper;


    @Transactional
    public Optional<OperationResult> processOperation(GenericOperationDTO operationDTO) {
        UUID accountId = null;
        OperationResult result;
        result = TransferOperationResultDTO.builder()
                .operationId(operationDTO.getId())
                .message("Operation " +operationDTO.getOperationType().name()+ " delivered")
                .status(OperationStatus.PENDING)
                .build();

        saveOutboxEvent(accountId, operationDTO);

        return Optional.of(result);
    }


    private void saveOutboxEvent(UUID accountId, GenericOperationDTO operationDTO) {
        try {
            OutboxEventsRecord event = new OutboxEventsRecord(UuidCreator.getTimeOrderedEpoch(), "ACCOUNT", accountId,
                    operationDTO.getId(), operationDTO.getOperationType().name(), objectMapper.writeValueAsString(operationDTO),
                    OperationStatus.PENDING.name(), 0, null, null, LocalDateTime.now(),
                    LocalDateTime.now());
            eventRepository.insert(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize payload", e);
        }
    }

}
