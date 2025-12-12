package net.microfin.financeapp.service;


import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.domain.OutboxEvent;
import net.microfin.financeapp.repository.OutboxEventRepository;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RetryService {

    private final OutboxEventRepository outboxEventRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleRetry(UUID outboxEventId) {
        OutboxEvent outboxEvent = outboxEventRepository.findByIdForUpdateSkipLocked(outboxEventId)
                .orElseThrow(() -> new RuntimeException("OutBox not Found " + outboxEventId));
        outboxEvent.setRetryCount(outboxEvent.getRetryCount() + 1);
        outboxEvent.setLastAttemptAt(LocalDateTime.now());
        if (outboxEvent.getRetryCount() >= 5) {
            outboxEvent.setStatus(OperationStatus.FAILED);
            outboxEvent.setNextAttemptAt(null);
        } else {
            outboxEvent.setNextAttemptAt(LocalDateTime.now().plusMinutes(5L * outboxEvent.getRetryCount()));
            outboxEvent.setStatus(OperationStatus.RETRYABLE);
        }
        outboxEventRepository.save(outboxEvent);
        log.error("Error occurred for event {}, exception", outboxEvent);
    }
}