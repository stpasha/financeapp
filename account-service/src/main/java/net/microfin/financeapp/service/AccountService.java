package net.microfin.financeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.AccType;
import net.microfin.financeapp.domain.Account;
import net.microfin.financeapp.domain.OutboxEvent;
import net.microfin.financeapp.domain.User;
import net.microfin.financeapp.dto.AccountDTO;
import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.ExchangeOperationDTO;
import net.microfin.financeapp.dto.TransferOperationDTO;
import net.microfin.financeapp.exception.AccountNotFoundException;
import net.microfin.financeapp.exception.InsufficientFundsException;
import net.microfin.financeapp.exception.InvalidPayloadException;
import net.microfin.financeapp.mapper.AccountMapper;
import net.microfin.financeapp.repository.AccountRepository;
import net.microfin.financeapp.repository.UserRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Validator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final UserRepository userRepository;
    private final Validator validator;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<AccountDTO> getAccountsByUserId(UUID userId) {
        return accountMapper.toDtoList(accountRepository.findAccountsByUserId(userId));
    }

    @Transactional(readOnly = true)
    public Optional<AccountDTO> getAccount(UUID id) {
        return accountRepository.findById(id).map(accountMapper::toDto);
    }

    @Transactional
    public Optional<AccountDTO> createAccount(AccountDTO accountDTO) {
        var violations = validator.validate(accountDTO);
        if (!violations.isEmpty()) {
            throw new InvalidPayloadException(violations.toString());
        }
        Optional<User> userOptional = userRepository.findById(accountDTO.getUserId());
        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not provided for account"));
        Account account = accountMapper.toEntity(accountDTO);
        account.setUser(user);
        Account saved = accountRepository.save(account);
        return Optional.of(accountMapper.toDto(saved));
    }

    @Transactional
    public void disable(Integer id) {
        accountRepository.disableAccount(id);
    }

    @Transactional
    public void processCashDeposit(OutboxEvent outboxEvent) {
        CashOperationDTO cashDeposit = fromJson(outboxEvent.getPayload(), CashOperationDTO.class);
        var violations = validator.validate(cashDeposit);
        if (!violations.isEmpty()) {
            throw new InvalidPayloadException(violations.toString());
        }
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
            }).orElseThrow(() -> new AccountNotFoundException("Account not found" + cashDeposit.getAccountId()));
        }
    }

    @Transactional
    public void processCashWithdraw(OutboxEvent outboxEvent) {
        CashOperationDTO cashWithdraw = fromJson(outboxEvent.getPayload(), CashOperationDTO.class);
        var violations = validator.validate(cashWithdraw);
        if (!violations.isEmpty()) {
            throw new InvalidPayloadException(violations.toString());
        }
        if (cashWithdraw.getAccountId() == null) {
            throw new AccountNotFoundException("Account not found" + cashWithdraw.getAccountId());
        } else {
            accountRepository.findByIdForUpdate(cashWithdraw.getAccountId()).map(account -> {
                if (account.getBalance().compareTo(cashWithdraw.getAmount()) < 0) {
                    throw new InsufficientFundsException("Insufficient funds in the account" + account.getId());
                }
                account.setBalance(account.getBalance().subtract(cashWithdraw.getAmount()));
                return accountRepository.save(account);
            }).orElseThrow(() -> new AccountNotFoundException("Account not found" + cashWithdraw.getAccountId()));
        }
    }

    @Transactional
    public void processExchange(OutboxEvent outboxEvent) {
        ExchangeOperationDTO exchange = fromJson(outboxEvent.getPayload(), ExchangeOperationDTO.class);
        if (exchange.getSourceAccountId() == null || exchange.getTargetAccountId() == null) {
            throw new AccountNotFoundException("Account not found" + exchange.getSourceAccountId() + " " + exchange.getTargetAccountId());
        } else {
            var violations = validator.validate(exchange);
            if (!violations.isEmpty()) {
                throw new InvalidPayloadException(violations.toString());
            }
            Account depositAccount = null;
            Account withdrawAccount = null;
            if (exchange.getSourceAccountId().equals(exchange.getTargetAccountId())) {
                throw new IllegalArgumentException("Source and target account cannot be the same");
            }

            UUID max = exchange.getSourceAccountId().timestamp() > exchange.getTargetAccountId().timestamp() ? exchange.getSourceAccountId() : exchange.getTargetAccountId();
            Pair<UUID, AccType> firstAccount = Pair.of(
                    max,
                    max == exchange.getSourceAccountId() ? AccType.SOURCE : AccType.TARGET
            );
            UUID min = exchange.getSourceAccountId().timestamp() > exchange.getTargetAccountId().timestamp() ? exchange.getTargetAccountId() : exchange.getSourceAccountId();
            Pair<UUID, AccType> secondAccount = Pair.of(
                    min,
                    min == exchange.getSourceAccountId() ? AccType.SOURCE : AccType.TARGET
            );
            if(AccType.SOURCE.equals(firstAccount.getRight())) {
                withdrawAccount = withdraw(firstAccount.getLeft(), exchange.getAmount());
            } else {
                depositAccount = deposit(firstAccount.getLeft(), exchange.getAmount());
            }
            if(AccType.SOURCE.equals(secondAccount.getRight())) {
                withdrawAccount = withdraw(secondAccount.getLeft(), exchange.getAmount());
            } else {
                depositAccount = deposit(secondAccount.getLeft(), exchange.getAmount());
            }

            if (firstAccount.getRight() == secondAccount.getRight()) {
                throw new IllegalStateException("Both accounts have same role (SOURCE/SOURCE or TARGET/TARGET)");
            }


            accountRepository.saveAll(List.of(Objects.requireNonNull(depositAccount), Objects.requireNonNull(withdrawAccount)));
        }
    }

    @Transactional
    public void processTransfer(OutboxEvent outboxEvent) {
        TransferOperationDTO transfer = fromJson(outboxEvent.getPayload(), TransferOperationDTO.class);
        if (transfer.getSourceAccountId() == null || transfer.getTargetAccountId() == null) {
            throw new AccountNotFoundException("Account not found" + transfer.getSourceAccountId() + " " + transfer.getTargetAccountId());
        } else {
            var violations = validator.validate(transfer);
            if (!violations.isEmpty()) {
                throw new InvalidPayloadException(violations.toString());
            }
            Account depositAccount = null;
            Account withdrawAccount = null;

            if (transfer.getSourceAccountId().equals(transfer.getTargetAccountId())) {
                throw new IllegalArgumentException("Source and target account cannot be the same");
            }

            UUID max = transfer.getSourceAccountId().timestamp() > transfer.getTargetAccountId().timestamp() ? transfer.getSourceAccountId() : transfer.getTargetAccountId();
            Pair<UUID, AccType> firstAccount = Pair.of(
                    max,
                    max == transfer.getSourceAccountId() ? AccType.SOURCE : AccType.TARGET
            );
            UUID min = transfer.getSourceAccountId().timestamp() > transfer.getTargetAccountId().timestamp() ? transfer.getTargetAccountId() : transfer.getSourceAccountId();
            Pair<UUID, AccType> secondAccount = Pair.of(
                    min,
                    min == transfer.getSourceAccountId() ? AccType.SOURCE : AccType.TARGET
            );
            if(AccType.SOURCE.equals(firstAccount.getRight())) {
                withdrawAccount = withdraw(firstAccount.getLeft(), transfer.getAmount());
            } else {
                depositAccount = deposit(firstAccount.getLeft(), transfer.getAmount());
            }
            if(AccType.SOURCE.equals(secondAccount.getRight())) {
                withdrawAccount = withdraw(secondAccount.getLeft(), transfer.getAmount());
            } else {
                depositAccount = deposit(secondAccount.getLeft(), transfer.getAmount());
            }

            if (firstAccount.getRight() == secondAccount.getRight()) {
                throw new IllegalStateException("Both accounts have same role (SOURCE/SOURCE or TARGET/TARGET)");
            }

            accountRepository.saveAll(List.of(Objects.requireNonNull(depositAccount), Objects.requireNonNull(withdrawAccount)));
        }
    }

    private Account deposit(UUID transfer, BigDecimal amount) {
        return accountRepository.findByIdForUpdate(transfer).map(account -> {
            account.setBalance(account.getBalance().add(amount));
            return account;
        }).orElseThrow(() -> new AccountNotFoundException("Account not found" + transfer));
    }

    private Account withdraw(UUID transfer, BigDecimal amount) {
        return accountRepository.findByIdForUpdate(transfer).map(account -> {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException("Insufficient funds in the account" + transfer);
            }
            account.setBalance(account.getBalance().subtract(amount));
            return account;
        }).orElseThrow(() -> new AccountNotFoundException("Account not found" + transfer));
    }

    private <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new InvalidPayloadException("Cannot deserialize payload", e);
        }
    }



}

