package net.microfin.financeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.AccType;
import net.microfin.financeapp.dto.AccountDTO;
import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.ExchangeOperationDTO;
import net.microfin.financeapp.dto.TransferOperationDTO;
import net.microfin.financeapp.exception.AccountNotFoundException;
import net.microfin.financeapp.exception.InsufficientFundsException;
import net.microfin.financeapp.exception.InvalidPayloadException;
import net.microfin.financeapp.jooq.tables.records.AccountsRecord;
import net.microfin.financeapp.jooq.tables.records.OutboxEventsRecord;
import net.microfin.financeapp.mapper.AccountMapper;
import net.microfin.financeapp.repository.AccountReadRepository;
import net.microfin.financeapp.repository.AccountWriteRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountWriteRepository accountWriteRepository;
    private final AccountMapper accountMapper;
    private final AccountReadRepository accountReadRepository;
    private final Validator validator;
    private final ObjectMapper objectMapper;

    public List<AccountDTO> getAccountsByUserId(UUID userId) {
        return accountMapper.toDTOList(accountReadRepository.findAccountsByUserId(userId));
    }

    public Optional<AccountDTO> getAccount(UUID id) {
        return accountReadRepository.findById(id).map(accountMapper::toDTO);
    }

    @Transactional
    public Optional<AccountDTO> createAccount(AccountDTO accountDTO) {
        var violations = validator.validate(accountDTO);
        if (!violations.isEmpty()) {
            throw new InvalidPayloadException(violations.toString());
        }
        AccountsRecord account = accountMapper.toRecord(accountDTO);
        AccountsRecord saved = accountWriteRepository.insert(account);
        return Optional.of(accountMapper.toDTO(saved));
    }

    @Transactional
    public void disable(UUID id) {
        accountWriteRepository.disableAccount(id);
    }

    @Transactional
    public void processCashDeposit(OutboxEventsRecord outboxEvent) {
        CashOperationDTO cashDeposit = fromJson(outboxEvent.getPayload(), CashOperationDTO.class);
        processValidation(cashDeposit);
        if (outboxEvent.getAccountId() == null) {
            depositNewAcc(cashDeposit);
        } else {
            depositExistingAcc(cashDeposit);
        }
    }

    private void processValidation(CashOperationDTO cashDeposit) {
        var violations = validator.validate(cashDeposit);
        if (!violations.isEmpty()) {
            throw new InvalidPayloadException(violations.toString());
        }
    }

    @Transactional
    public void processCashWithdraw(OutboxEventsRecord outboxEvent) {
        CashOperationDTO cashWithdraw = fromJson(outboxEvent.getPayload(), CashOperationDTO.class);
        processValidation(cashWithdraw);
        if (cashWithdraw.getAccountId() == null) {
            throw new AccountNotFoundException("Account not found" + cashWithdraw);
        } else {
            AccountsRecord account = accountWriteRepository.findByIdForUpdate(cashWithdraw.getAccountId()).orElseThrow(() -> new AccountNotFoundException("Account not found" + cashWithdraw.getAccountId()));
            if (account.getBalance().compareTo(cashWithdraw.getAmount()) < 0) {
                throw new InsufficientFundsException("Insufficient funds in the account" + account.getAccountId());
            }
            account.setBalance(account.getBalance().subtract(cashWithdraw.getAmount()));
            accountWriteRepository.updateBalance(account);
        }
    }

    @Transactional
    public void processExchange(OutboxEventsRecord outboxEvent) {
        ExchangeOperationDTO exchange = fromJson(outboxEvent.getPayload(), ExchangeOperationDTO.class);
        if (exchange.getSourceAccountId() == null || exchange.getTargetAccountId() == null) {
            throw new AccountNotFoundException("Account not found" + exchange.getSourceAccountId() + " " + exchange.getTargetAccountId());
        } else {
            var violations = validator.validate(exchange);
            if (!violations.isEmpty()) {
                throw new InvalidPayloadException(violations.toString());
            }
            AccountsRecord depositAccount = null;
            AccountsRecord withdrawAccount = null;
            if (exchange.getSourceAccountId().equals(exchange.getTargetAccountId())) {
                throw new IllegalArgumentException("Source and target account cannot be the same");
            }

            UUID sourceId = exchange.getSourceAccountId();
            UUID targetId = exchange.getTargetAccountId();

            boolean sourceFirst = sourceId.compareTo(targetId) > 0;

            Pair<UUID, AccType> firstAccount = Pair.of(
                    sourceFirst ? sourceId : targetId,
                    sourceFirst ? AccType.SOURCE : AccType.TARGET
            );

            Pair<UUID, AccType> secondAccount = Pair.of(
                    sourceFirst ? targetId : sourceId,
                    sourceFirst ? AccType.TARGET : AccType.SOURCE
            );

            if(AccType.SOURCE.equals(firstAccount.getSecond())) {
                withdrawAccount = withdraw(firstAccount.getFirst(), exchange.getAmount());
            } else {
                depositAccount = deposit(firstAccount.getFirst(), exchange.getAmount());
            }
            if(AccType.SOURCE.equals(secondAccount.getSecond())) {
                withdrawAccount = withdraw(secondAccount.getFirst(), exchange.getAmount());
            } else {
                depositAccount = deposit(secondAccount.getFirst(), exchange.getAmount());
            }

            if (firstAccount.getSecond() == secondAccount.getSecond()) {
                throw new IllegalStateException("Both accounts have same role (SOURCE/SOURCE or TARGET/TARGET)");
            }


            accountWriteRepository.updateAllBalances(List.of(Objects.requireNonNull(depositAccount), Objects.requireNonNull(withdrawAccount)));
        }
    }

    @Transactional
    public void processTransfer(OutboxEventsRecord outboxEvent) {
        TransferOperationDTO transfer = fromJson(outboxEvent.getPayload(), TransferOperationDTO.class);
        if (transfer.getSourceAccountId() == null || transfer.getTargetAccountId() == null) {
            throw new AccountNotFoundException("Account not found" + transfer.getSourceAccountId() + " " + transfer.getTargetAccountId());
        } else {
            var violations = validator.validate(transfer);
            if (!violations.isEmpty()) {
                throw new InvalidPayloadException(violations.toString());
            }
            AccountsRecord depositAccount = null;
            AccountsRecord withdrawAccount = null;

            if (transfer.getSourceAccountId().equals(transfer.getTargetAccountId())) {
                throw new IllegalArgumentException("Source and target account cannot be the same");
            }

            UUID sourceId = transfer.getSourceAccountId();
            UUID targetId = transfer.getTargetAccountId();

            boolean sourceFirst = sourceId.compareTo(targetId) > 0;

            Pair<UUID, AccType> firstAccount = Pair.of(
                    sourceFirst ? sourceId : targetId,
                    sourceFirst ? AccType.SOURCE : AccType.TARGET
            );

            Pair<UUID, AccType> secondAccount = Pair.of(
                    sourceFirst ? targetId : sourceId,
                    sourceFirst ? AccType.TARGET : AccType.SOURCE
            );

            if(AccType.SOURCE.equals(firstAccount.getSecond())) {
                withdrawAccount = withdraw(firstAccount.getFirst(), transfer.getAmount());
            } else {
                depositAccount = deposit(firstAccount.getFirst(), transfer.getAmount());
            }
            if(AccType.SOURCE.equals(secondAccount.getSecond())) {
                withdrawAccount = withdraw(secondAccount.getFirst(), transfer.getAmount());
            } else {
                depositAccount = deposit(secondAccount.getFirst(), transfer.getAmount());
            }

            if (firstAccount.getSecond() == secondAccount.getSecond()) {
                throw new IllegalStateException("Both accounts have same role (SOURCE/SOURCE or TARGET/TARGET)");
            }

            accountWriteRepository.updateAllBalances(List.of(Objects.requireNonNull(depositAccount), Objects.requireNonNull(withdrawAccount)));
        }
    }

    private AccountsRecord deposit(UUID transfer, BigDecimal amount) {
        AccountsRecord account = accountWriteRepository.findByIdForUpdate(transfer).orElseThrow(() -> new AccountNotFoundException("Account not found" + transfer));
        account.setBalance(account.getBalance().add(amount));
        return account;
    }

    private AccountsRecord withdraw(UUID transfer, BigDecimal amount) {
        return accountWriteRepository.findByIdForUpdate(transfer).map(account -> {
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

    private void depositExistingAcc(CashOperationDTO cashDeposit) {
        accountWriteRepository.findByIdForUpdate(cashDeposit.getAccountId()).map(account -> {
            account.setBalance(account.getBalance().add(cashDeposit.getAmount()));
            accountWriteRepository.updateBalance(account);
            return account;
        }).orElseThrow(() -> new AccountNotFoundException("Account not found" + cashDeposit.getAccountId()));
    }

    private void depositNewAcc(CashOperationDTO cashDeposit) {
        AccountsRecord account = accountWriteRepository.findActiveByUserAndCurrencyForUpdate(
                cashDeposit.getUserId(),
                cashDeposit.getCurrencyCode()
        ).orElseGet(() -> createAccount(cashDeposit));
        account.setBalance(account.getBalance().add(cashDeposit.getAmount()));
        accountWriteRepository.updateBalance(account);
    }

    private AccountsRecord createAccount(CashOperationDTO cashDeposit) {
        LocalDateTime operationDate = LocalDateTime.now();
        AccountsRecord accountsRecord = new AccountsRecord(null, cashDeposit.getUserId(), BigDecimal.ZERO, cashDeposit.getCurrencyCode().getName(), true, operationDate, operationDate);
        return accountWriteRepository.insert(accountsRecord);
    }


}

