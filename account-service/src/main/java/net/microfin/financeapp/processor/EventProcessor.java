package net.microfin.financeapp.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.domain.IdempotencyRecord;
import net.microfin.financeapp.domain.OutboxEvent;
import net.microfin.financeapp.service.AccountService;
import net.microfin.financeapp.service.IdempotencyService;
import net.microfin.financeapp.service.OutboxService;
import net.microfin.financeapp.service.OutboxUserService;
import net.microfin.financeapp.util.OperationType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventProcessor {
    private final AccountService accountService;
    private final OutboxUserService outboxUserService;
    private final IdempotencyService idempotencyService;
    private final OutboxService outboxService;
    public static final int TTL = 7;

    @Transactional
    public void processSingleEvent(UUID uuid) {
        Optional<OutboxEvent> outboxWithSkipLock = outboxService.findOutboxWithSkipLock(uuid);
        if (outboxWithSkipLock.isEmpty()) {
            return;
        }
        if (!idempotencyService.tryStart(IdempotencyRecord.builder()
                .createdAt(LocalDateTime.now())
                .expireAt(LocalDateTime.now().plusDays(TTL))
                .outboxId(uuid)
                .build())
        ) {
            return;
        }
        outboxWithSkipLock.ifPresent(outbox -> {
            outbox = outboxService.registerSynchronization(outbox);
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
}
