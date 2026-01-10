package net.microfin.financeapp.processor;

import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.jooq.tables.records.IdempotencyRecordsRecord;
import net.microfin.financeapp.jooq.tables.records.OutboxEventsRecord;
import net.microfin.financeapp.service.AccountService;
import net.microfin.financeapp.service.IdempotencyService;
import net.microfin.financeapp.service.OutboxService;
import net.microfin.financeapp.service.OutboxUserService;
import net.microfin.financeapp.util.OperationType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class EventProcessor {
    private final AccountService accountService;
    private final OutboxUserService outboxUserService;
    private final IdempotencyService idempotencyService;
    private final OutboxService outboxService;
    private final Long ttlDays;

    public EventProcessor(AccountService accountService,
                          OutboxUserService outboxUserService,
                          IdempotencyService idempotencyService,
                          OutboxService outboxService,
                          @Value("${spring.scheduler.ttl.idempotency:30}") Long ttlDays) {
        this.accountService = accountService;
        this.outboxUserService = outboxUserService;
        this.idempotencyService = idempotencyService;
        this.outboxService = outboxService;
        this.ttlDays = ttlDays;
    }

    @Transactional
    public void processSingleEvent(UUID uuid) {
        Optional<OutboxEventsRecord> outboxWithSkipLock = outboxService.findOutboxWithSkipLock(uuid);
        if (outboxWithSkipLock.isEmpty()) {
            return;
        }
        IdempotencyRecordsRecord idempotencyRecordsRecord = new IdempotencyRecordsRecord(
                uuid, LocalDateTime.now() ,LocalDateTime.now().plusDays(ttlDays));

        if (!idempotencyService.tryStart(idempotencyRecordsRecord)) {
            return;
        }
        outboxWithSkipLock.ifPresent(outbox -> {
            outbox = outboxService.registerSynchronization(outbox);
            switch (OperationType.valueOf(outbox.getOperationType())) {
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
            outboxService.markSent(outbox.getOutboxId());
        });

    }
}
