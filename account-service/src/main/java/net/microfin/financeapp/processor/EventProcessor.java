package net.microfin.financeapp.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.domain.Account;
import net.microfin.financeapp.domain.OutboxEvent;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.repository.AccountRepository;
import net.microfin.financeapp.repository.OutboxEventRepository;
import net.microfin.financeapp.repository.UserRepository;
import net.microfin.financeapp.service.KeycloakUserService;
import net.microfin.financeapp.service.RetryService;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
@AllArgsConstructor
@Slf4j
public class EventProcessor {
    private final OutboxEventRepository outboxEventRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final KeycloakUserService keycloakUserService;
    private final RetryService retryService;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void process() {
        for (OutboxEvent outboxEvent : outboxEventRepository.findOutboxEventByPendingStatus()) {
            switch (outboxEvent.getOperationType()) {
                case OperationType.USER_CREATED -> {
                    processUserCreateEvent(outboxEvent);
                }
                case OperationType.PASSWORD_CHANGED -> {
                    processChangePasswordEvent(outboxEvent);
                }
                case OperationType.CASH_DEPOSIT -> {
                    processCashDeposit(outboxEvent);
                }
                case OperationType.CASH_WITHDRAWAL -> {
                    processCashWithdraw(outboxEvent);
                }
                case OperationType.EXCHANGE -> {
                    processExchange(outboxEvent);
                }
                case OperationType.TRANSFER -> {
                    processTransfer(outboxEvent);
                }
            }
        }
    }

    private void processUserCreateEvent(OutboxEvent outboxEvent) {
        try {
            UserDTO userDTO = fromJson(outboxEvent.getPayload(), UserDTO.class);
            UserRepresentation keycloakUser = keycloakUserService.createUser(userDTO);
            log.info("Keycloak response: {}", keycloakUser);
            outboxEvent.setStatus(OperationStatus.SENT);
            userRepository.findById(userDTO.getId()).map(user -> {
                user.setEnabled(true);
                user.setKeycloakId(UUID.fromString(keycloakUser.getId()));
                userRepository.save(user);
                log.info("Processed successfully Outbox event result - {} \n userDTO - {}", outboxEvent, userDTO);
                return user;
            }).orElseThrow(() -> new EntityNotFoundException("User not found"));
        } catch (Exception e) {
            retryService.handleRetry(outboxEvent, e);
        }
        outboxEventRepository.save(outboxEvent);
    }

    private void processChangePasswordEvent(OutboxEvent outboxEvent) {
        try {
            PasswordDTO passwordDTO = fromJson(outboxEvent.getPayload(), PasswordDTO.class);
            userRepository.findById(passwordDTO.getId()).map(user -> {
                passwordDTO.setKeycloakId(user.getKeycloakId());
                keycloakUserService.updateUserPassword(passwordDTO);
                userRepository.save(user);
                log.info("Processed successfully for password Outbox event result - {}", outboxEvent);
                return user;
            }).orElseThrow(() -> new EntityNotFoundException("User not found for password update"));
            outboxEvent.setStatus(OperationStatus.SENT);
        } catch (Exception e) {
            retryService.handleRetry(outboxEvent, e);
        }
        outboxEventRepository.save(outboxEvent);
    }

    private void processCashDeposit(OutboxEvent outboxEvent) {
        try {
            CashOperationDTO cashDeposit = fromJson(outboxEvent.getPayload(), CashOperationDTO.class);
            if (outboxEvent.getAccountId() == null) {
                userRepository.findById(cashDeposit.getUserId()).map(user -> accountRepository.save(Account.builder()
                        .active(true)
                        .balance(cashDeposit.getAmount())
                        .currencyCode(cashDeposit.getCurrencyCode())
                        .user(user).build())).orElseThrow(() -> new RuntimeException("User not found"));

            } else {
                accountRepository.findById(cashDeposit.getAccountId()).map(account -> {
                    account.setBalance(account.getBalance().add(cashDeposit.getAmount()));
                    return account;
                }).orElseThrow(() -> new RuntimeException("Account not found"));
            }
            outboxEvent.setStatus(OperationStatus.SENT);
        } catch (Exception e) {
            retryService.handleRetry(outboxEvent, e);
        }
    }

    private void processCashWithdraw(OutboxEvent outboxEvent) {
        try {
            CashOperationDTO cashWithdraw = fromJson(outboxEvent.getPayload(), CashOperationDTO.class);
            if (cashWithdraw.getAccountId() == null) {
                throw new RuntimeException("Account not found");
            } else {
                accountRepository.findById(cashWithdraw.getAccountId()).map(account -> {
                    if (account.getBalance().compareTo(cashWithdraw.getAmount()) < 0) {
                        throw new RuntimeException("Insufficient funds in the account");
                    }
                    account.setBalance(account.getBalance().subtract(cashWithdraw.getAmount()));
                    return accountRepository.save(account);
                }).orElseThrow(() -> new RuntimeException("Account not found"));
                outboxEvent.setStatus(OperationStatus.SENT);
            }
        } catch (Exception e) {
            retryService.handleRetry(outboxEvent, e);
        }
    }

    private void processExchange(OutboxEvent outboxEvent) {
        try {
            ExchangeOperationDTO exchange = fromJson(outboxEvent.getPayload(), ExchangeOperationDTO.class);
            if (exchange.getSourceAccountId() == null || exchange.getTargetAccountId() == null) {
                throw new RuntimeException("Account not found");
            } else {
                accountRepository.findById(exchange.getSourceAccountId()).map(account -> {
                    if (account.getBalance().compareTo(exchange.getAmount()) < 0) {
                        throw new RuntimeException("Insufficient funds in the account");
                    }
                    account.setBalance(account.getBalance().subtract(exchange.getAmount()));
                    return account;
                }).orElseThrow(() -> new RuntimeException("Account not found"));
                accountRepository.findById(exchange.getTargetAccountId()).map(account -> {
                    account.setBalance(account.getBalance().add(exchange.getTargetAmount()));
                    return account;
                }).orElseThrow(() -> new RuntimeException("Account not found"));
                outboxEvent.setStatus(OperationStatus.SENT);
            }
        } catch (Exception e) {
            retryService.handleRetry(outboxEvent, e);
        }
    }

    private void processTransfer(OutboxEvent outboxEvent) {
        try {
            TransferOperationDTO transfer = fromJson(outboxEvent.getPayload(), TransferOperationDTO.class);
            if (transfer.getSourceAccountId() == null || transfer.getTargetAccountId() == null) {
                throw new RuntimeException("Account not found");
            } else {
                accountRepository.findById(transfer.getSourceAccountId()).map(account -> {
                    if (account.getBalance().compareTo(transfer.getAmount()) < 0) {
                        throw new RuntimeException("Insufficient funds in the account");
                    }
                    account.setBalance(account.getBalance().subtract(transfer.getAmount()));
                    return account;
                }).orElseThrow(() -> new RuntimeException("Account not found"));
                accountRepository.findById(transfer.getTargetAccountId()).map(account -> {
                    account.setBalance(account.getBalance().add(transfer.getAmount()));
                    return account;
                }).orElseThrow(() -> new RuntimeException("Account not found"));
                outboxEvent.setStatus(OperationStatus.SENT);
            }
        } catch (Exception e) {
            retryService.handleRetry(outboxEvent, e);
        }
    }

    @Scheduled(fixedDelay = 15000)
    @Transactional
    public void processErrorEvents() {
        for (OutboxEvent outboxEvent : outboxEventRepository.findRetryableOutboxEvent(LocalDateTime.now())) {
            switch (outboxEvent.getOperationType()) {
                case OperationType.USER_CREATED -> {
                    processUserCreateEvent(outboxEvent);
                }
                case OperationType.PASSWORD_CHANGED -> {
                    processChangePasswordEvent(outboxEvent);
                }
            }
        }
    }

    private <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot deserialize payload", e);
        }
    }
}
