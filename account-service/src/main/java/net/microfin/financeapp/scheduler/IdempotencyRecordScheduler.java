package net.microfin.financeapp.scheduler;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.service.IdempotencyService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
public class IdempotencyRecordScheduler {

    private final IdempotencyService idempotencyService;

    @Scheduled(fixedDelay = 15000)
    public void deleteIdempotencyRecords() {
        idempotencyService.deleteAllOutdated();
    }
}
