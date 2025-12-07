package net.microfin.financeapp.service;


import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.domain.OutboxEvent;
import net.microfin.financeapp.repository.OutboxEventRepository;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RetryService {

    private final OutboxEventRepository outboxEventRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleRetry(OutboxEvent outboxEvent, Exception e) {
        outboxEvent.setRetryCount(outboxEvent.getRetryCount() + 1);
        outboxEvent.setLastAttemptAt(LocalDateTime.now());
        outboxEvent.setNextAttemptAt(LocalDateTime.now().plusMinutes(5));
        if (outboxEvent.getRetryCount() > 5) {
            outboxEvent.setStatus(OperationStatus.FAILED);
        } else {
            outboxEvent.setStatus(OperationStatus.RETRYABLE);
        }
        outboxEventRepository.save(outboxEvent);
        log.error("Error occurred for event {}, exception", outboxEvent, e);
    }
}