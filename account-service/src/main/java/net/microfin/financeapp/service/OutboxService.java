package net.microfin.financeapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.jooq.tables.records.OutboxEventsRecord;
import net.microfin.financeapp.repository.OutboxEventWriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Optional;
import java.util.UUID;

import static net.microfin.financeapp.util.OperationStatus.PROCESSING;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {

    private final RetryService retryService;
    private final OutboxEventWriteRepository outboxEventWriteRepository;

    public void markSent(UUID eventId) {
        outboxEventWriteRepository.markSent(eventId);
    }

    public OutboxEventsRecord registerSynchronization(OutboxEventsRecord outboxEvent) {
        OutboxEventsRecord processingEvent = outboxEventWriteRepository.updateStatus(outboxEvent.getOutboxId(), PROCESSING);
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCompletion(int status) {
                        if (status != STATUS_COMMITTED) {
                            log.warn("Outbox event {} rolled back, scheduling retry", outboxEvent.getOutboxId());
                            retryService.handleRetry(outboxEvent.getOutboxId());
                        }
                    }
                }
        );
        return processingEvent;
    }

    public Optional<OutboxEventsRecord> findOutboxWithSkipLock(UUID uuid) {
        return outboxEventWriteRepository.findByIdForUpdateSkipLocked(uuid);
    }

}
