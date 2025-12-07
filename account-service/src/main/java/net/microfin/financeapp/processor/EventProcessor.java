package net.microfin.financeapp.processor;

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

@Component
@ConditionalOnProperty(prefix = "scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
@AllArgsConstructor
@Slf4j
public class EventProcessor {
    private final OutboxEventRepository outboxEventRepository;
    private final AccountService accountService;
    private final OutboxUserService outboxUserService;

    @Scheduled(fixedDelay = 5000)
    public void process() {
        for (OutboxEvent outboxEvent : outboxEventRepository.findOutboxEventByPendingStatus()) {
            switch (outboxEvent.getOperationType()) {
                case OperationType.USER_CREATED -> {
                    outboxUserService.processUserCreateEvent(outboxEvent);
                }
                case OperationType.PASSWORD_CHANGED -> {
                    outboxUserService.processChangePasswordEvent(outboxEvent);
                }
                case OperationType.CASH_DEPOSIT -> {
                    accountService.processCashDeposit(outboxEvent);
                }
                case OperationType.CASH_WITHDRAWAL -> {
                    accountService.processCashWithdraw(outboxEvent);
                }
                case OperationType.EXCHANGE -> {
                    accountService.processExchange(outboxEvent);
                }
                case OperationType.TRANSFER -> {
                    accountService. processTransfer(outboxEvent);
                }
            }
        }
    }


    @Scheduled(fixedDelay = 15000)
    @Transactional
    public void processErrorEvents() {
        for (OutboxEvent outboxEvent : outboxEventRepository.findRetryableOutboxEvent(LocalDateTime.now())) {
            switch (outboxEvent.getOperationType()) {
                case OperationType.USER_CREATED -> {
                    outboxUserService.processUserCreateEvent(outboxEvent);
                }
                case OperationType.PASSWORD_CHANGED -> {
                    outboxUserService.processChangePasswordEvent(outboxEvent);
                }
            }
        }
    }


}
