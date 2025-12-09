package net.microfin.financeapp.service;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.domain.OutboxEvent;
import net.microfin.financeapp.repository.OutboxEventRepository;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final RetryService retryService;
    private final OutboxEventRepository outboxEventRepository;

    public void markSent(OutboxEvent event) {
        event.setStatus(OperationStatus.SENT);
        outboxEventRepository.save(event);
    }

    public void registerSynchronization(OutboxEvent outboxEvent) {
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
    }

    public Optional<OutboxEvent> findOutboxWithSkipLock(OutboxEvent outboxEvent) {
        return outboxEventRepository.findByIdForUpdate(outboxEvent.getId());
    }

}
