package net.microfin.financeapp.service;


import net.microfin.financeapp.jooq.tables.records.OutboxEventsRecord;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.repository.OutboxEventWriteRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RetryService {

    private final OutboxEventWriteRepository outboxEventWriteRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleRetry(UUID outboxEventId) {
        OutboxEventsRecord outboxEvent = outboxEventWriteRepository.findByIdForUpdateSkipLocked(outboxEventId)
                .orElseThrow(() -> new RuntimeException("OutBox not Found " + outboxEventId));
        int retryCount = outboxEvent.getRetryCount() + 1;
        if (outboxEvent.getRetryCount() >= 5) {
            outboxEventWriteRepository.markFailed(outboxEvent.getOutboxId(), retryCount);
        } else {
            outboxEventWriteRepository.markRetryable(outboxEvent.getOutboxId(), retryCount);
        }

        log.error("Error occurred for event {}, exception", outboxEvent);
    }
}