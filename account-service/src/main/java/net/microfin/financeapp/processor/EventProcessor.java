package net.microfin.financeapp.processor;

import net.microfin.financeapp.domain.IdempotencyRecord;
import net.microfin.financeapp.service.IdempotencyService;
import net.microfin.financeapp.service.OutboxService;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.domain.OutboxEvent;
import net.microfin.financeapp.repository.OutboxEventRepository;
import net.microfin.financeapp.service.AccountService;
import net.microfin.financeapp.service.OutboxUserService;
import net.microfin.financeapp.util.OperationType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@ConditionalOnProperty(prefix = "scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
@AllArgsConstructor
@Slf4j
public class EventProcessor {
    private final OutboxEventRepository outboxEventRepository;
    private final AccountService accountService;
    private final OutboxUserService outboxUserService;
    private final IdempotencyService idempotencyService;
    private final OutboxService outboxService;
    public static final int TTL = 7;



    @Scheduled(fixedDelay = 5000)
    public void process() {
        for (OutboxEvent outboxEvent : outboxEventRepository.findOutboxEventByPendingStatus(Pageable.ofSize(1000))) {
            processSingleEvent(outboxEvent);
        }
    }

    @Transactional
    private void processSingleEvent(OutboxEvent outboxEvent) {
        if (!idempotencyService.tryStart(IdempotencyRecord.builder()
                .createdAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now().plusDays(TTL))
                .outboxId(outboxEvent.getId())
                .build())
        ) {
            return;
        }
        Optional<OutboxEvent> outboxWithScipLock = outboxService.findOutboxWithSkipLock(outboxEvent);
        outboxWithScipLock.ifPresent(outbox -> {
            outboxService.registerSynchronization(outbox);
            switch (outbox.getOperationType()) {
                case OperationType.USER_CREATED -> {
                    outboxUserService.processUserCreateEvent(outbox);
                }
                case OperationType.PASSWORD_CHANGED -> {
                    outboxUserService.processChangePasswordEvent(outbox);
                }
                case OperationType.CASH_DEPOSIT -> {
                    accountService.processCashDeposit(outbox);
                }
                case OperationType.CASH_WITHDRAWAL -> {
                    accountService.processCashWithdraw(outbox);
                }
                case OperationType.EXCHANGE -> {
                    accountService.processExchange(outbox);
                }
                case OperationType.TRANSFER -> {
                    accountService. processTransfer(outbox);
                }
            }
            outboxService.markSent(outbox);
        });

    }


    @Scheduled(fixedDelay = 15000)
    @Transactional
    public void processErrorEvents() {
        for (OutboxEvent outboxEvent : outboxEventRepository.findRetryableOutboxEvent(LocalDateTime.now(), Pageable.ofSize(1000))) {
            processSingleEvent(outboxEvent);
        }
    }


}
