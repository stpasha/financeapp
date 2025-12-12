package net.microfin.financeapp.service;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.domain.OutboxEvent;
import net.microfin.financeapp.repository.OutboxEventRepository;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Optional;
import java.util.UUID;

import static net.microfin.financeapp.util.OperationStatus.PROCESSING;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final RetryService retryService;
    private final OutboxEventRepository outboxEventRepository;

    public void markSent(OutboxEvent event) {
        event.setLastAttemptAt(null);
        event.setNextAttemptAt(null);
        event.setRetryCount(0);
        event.setStatus(OperationStatus.SENT);
        outboxEventRepository.save(event);
    }

    public OutboxEvent registerSynchronization(OutboxEvent outboxEvent) {
        outboxEvent.setStatus(PROCESSING);
        OutboxEvent processingEvent = outboxEventRepository.save(outboxEvent);
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCompletion(int status) {
                        if (status != STATUS_COMMITTED) {
                            retryService.handleRetry(outboxEvent.getId());
                        }
                    }
                }
        );
        return processingEvent;
    }

    public Optional<OutboxEvent> findOutboxWithSkipLock(UUID uuid) {
        return outboxEventRepository.findByIdForUpdateSkipLocked(uuid);
    }

}
