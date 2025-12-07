package net.microfin.financeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.AccType;
import net.microfin.financeapp.domain.Account;
import net.microfin.financeapp.domain.OutboxEvent;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.mapper.AccountMapper;
import net.microfin.financeapp.repository.AccountRepository;
import net.microfin.financeapp.repository.OutboxEventRepository;
import net.microfin.financeapp.repository.UserRepository;
import net.microfin.financeapp.util.OperationStatus;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final UserRepository userRepository;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    public List<AccountDTO> getAccountsByUserId(Integer userId) {
        return accountMapper.toDtoList(accountRepository.findAccountsByUserId(userId));
    }

    public Optional<AccountDTO> getAccount(Integer id) {
        return accountRepository.findById(id).map(accountMapper::toDto);
    }

    @Transactional
    public Optional<AccountDTO> createAccount(AccountDTO accountDTO) {
        Account account = accountMapper.toEntity(accountDTO);
        Account saved = accountRepository.save(account);
        return Optional.of(accountMapper.toDto(saved));
    }

    @Transactional
    public void disable(Integer id) {
        accountRepository.disableAccount(id);
    }

    @Transactional
    public void processCashDeposit(OutboxEvent outboxEvent) {
        outboxService.registerSynchronization(outboxEvent);
        CashOperationDTO cashDeposit = fromJson(outboxEvent.getPayload(), CashOperationDTO.class);
        if (outboxEvent.getAccountId() == null) {
            userRepository.findById(cashDeposit.getUserId()).map(user -> accountRepository.save(Account.builder()
                    .active(true)
                    .balance(cashDeposit.getAmount())
                    .currencyCode(cashDeposit.getCurrencyCode())
                    .user(user).build())).orElseThrow(() -> new RuntimeException("User not found"));

        } else {
            accountRepository.findByIdForUpdate(cashDeposit.getAccountId()).map(account -> {
                account.setBalance(account.getBalance().add(cashDeposit.getAmount()));
                return account;
            }).orElseThrow(() -> new RuntimeException("Account not found"));
        }
        outboxService.markSent(outboxEvent);
    }

    @Transactional
    public void processCashWithdraw(OutboxEvent outboxEvent) {
        outboxService.registerSynchronization(outboxEvent);
        CashOperationDTO cashWithdraw = fromJson(outboxEvent.getPayload(), CashOperationDTO.class);
        if (cashWithdraw.getAccountId() == null) {
            throw new RuntimeException("Account not found");
        } else {
            accountRepository.findByIdForUpdate(cashWithdraw.getAccountId()).map(account -> {
                if (account.getBalance().compareTo(cashWithdraw.getAmount()) < 0) {
                    throw new RuntimeException("Insufficient funds in the account");
                }
                account.setBalance(account.getBalance().subtract(cashWithdraw.getAmount()));
                return accountRepository.save(account);
            }).orElseThrow(() -> new RuntimeException("Account not found"));
            outboxService.markSent(outboxEvent);
        }
    }

    @Transactional
    public void processExchange(OutboxEvent outboxEvent) {
        outboxService.registerSynchronization(outboxEvent);
        ExchangeOperationDTO exchange = fromJson(outboxEvent.getPayload(), ExchangeOperationDTO.class);
        if (exchange.getSourceAccountId() == null || exchange.getTargetAccountId() == null) {
            throw new RuntimeException("Account not found");
        } else {
            int max = Math.max(exchange.getSourceAccountId(), exchange.getTargetAccountId());
            Pair<Integer, AccType> firstAccount = Pair.of(
                    max,
                    max == exchange.getSourceAccountId() ? AccType.SOURCE : AccType.TARGET
            );
            int min = Math.min(exchange.getSourceAccountId(), exchange.getTargetAccountId());
            Pair<Integer, AccType> secondAccount = Pair.of(
                    min,
                    min == exchange.getSourceAccountId() ? AccType.SOURCE : AccType.TARGET
            );
            if(AccType.SOURCE.equals(firstAccount.getRight())) {
                withdraw(firstAccount.getLeft(), exchange.getAmount());
            } else {
                deposit(firstAccount.getLeft(), exchange.getAmount());
            }
            if(AccType.SOURCE.equals(secondAccount.getRight())) {
                withdraw(secondAccount.getLeft(), exchange.getAmount());
            } else {
                deposit(secondAccount.getLeft(), exchange.getAmount());
            }
            outboxService.markSent(outboxEvent);
        }
    }

    @Transactional
    public void processTransfer(OutboxEvent outboxEvent) {
        outboxService.registerSynchronization(outboxEvent);
        TransferOperationDTO transfer = fromJson(outboxEvent.getPayload(), TransferOperationDTO.class);
        if (transfer.getSourceAccountId() == null || transfer.getTargetAccountId() == null) {
            throw new RuntimeException("Account not found");
        } else {
            int max = Math.max(transfer.getSourceAccountId(), transfer.getTargetAccountId());
            Pair<Integer, AccType> firstAccount = Pair.of(
                    max,
                    max == transfer.getSourceAccountId() ? AccType.SOURCE : AccType.TARGET
            );
            int min = Math.min(transfer.getSourceAccountId(), transfer.getTargetAccountId());
            Pair<Integer, AccType> secondAccount = Pair.of(
                    min,
                    min == transfer.getSourceAccountId() ? AccType.SOURCE : AccType.TARGET
            );
            if(AccType.SOURCE.equals(firstAccount.getRight())) {
                withdraw(firstAccount.getLeft(), transfer.getAmount());
            } else {
                deposit(firstAccount.getLeft(), transfer.getAmount());
            }
            if(AccType.SOURCE.equals(secondAccount.getRight())) {
                withdraw(secondAccount.getLeft(), transfer.getAmount());
            } else {
                deposit(secondAccount.getLeft(), transfer.getAmount());
            }
            outboxService.markSent(outboxEvent);
        }
    }

    private void deposit(Integer transfer, BigDecimal amount) {
        accountRepository.findByIdForUpdate(transfer).map(account -> {
            account.setBalance(account.getBalance().add(amount));
            return account;
        }).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    private void withdraw(Integer transfer, BigDecimal amount) {
        accountRepository.findByIdForUpdate(transfer).map(account -> {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient funds in the account");
            }
            account.setBalance(account.getBalance().subtract(amount));
            return account;
        }).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    private <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot deserialize payload", e);
        }
    }



}

