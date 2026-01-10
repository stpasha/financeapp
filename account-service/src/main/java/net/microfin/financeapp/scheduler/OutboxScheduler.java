package net.microfin.financeapp.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.processor.EventProcessor;
import net.microfin.financeapp.repository.OutboxEventWriteRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxEventWriteRepository outboxEventWriteRepository;
    private final EventProcessor eventProcessor;


    @Scheduled(fixedDelay = 5000)
    public void process() {
        for (UUID id : outboxEventWriteRepository.findOutboxEventIdByPendingStatus(1000)) {
            eventProcessor.processSingleEvent(id);
        }
    }

    @Scheduled(fixedDelay = 15000)
    public void processErrorEvents() {
        for (UUID id : outboxEventWriteRepository.findRetryableOutboxEvent(LocalDateTime.now(), 1000)) {
            eventProcessor.processSingleEvent(id);
        }
    }
}
