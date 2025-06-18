package net.microfin.financeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.domain.Account;
import net.microfin.financeapp.domain.OutboxEvent;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.mapper.AccountMapper;
import net.microfin.financeapp.repository.AccountRepository;
import net.microfin.financeapp.repository.OutboxEventRepository;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultAccountService implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final OutboxEventRepository eventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<AccountDTO> getAccountsByUserId(Integer userId) {
        return accountMapper.toDtoList(accountRepository.findAccountsByUserId(userId));
    }

    @Override
    public Optional<AccountDTO> getAccount(Integer id) {
        return accountRepository.findById(id).map(accountMapper::toDto);
    }

    @Override
    @Transactional
    public Optional<AccountDTO> createAccount(AccountDTO accountDTO) {
        Account account = accountMapper.toEntity(accountDTO);
        Account saved = accountRepository.save(account);
        return Optional.of(accountMapper.toDto(saved));
    }

    @Override
    @Transactional
    public void disable(Integer id) {
        accountRepository.disableAccount(id);
    }

    @Override
    @Transactional
    public Optional<OperationResult> processOperation(GenericOperationDTO operationDTO) {
        Integer accountId = null;
        OperationResult result = null;
                switch (operationDTO.getOperationType()) {
            case CASH_DEPOSIT, CASH_WITHDRAWAL -> {
                CashOperationDTO cashOperation = (CashOperationDTO) operationDTO;
                Optional<Account> account = accountRepository.findByCurrencyCodeAndUserId(
                        cashOperation.getCurrencyCode(),
                        cashOperation.getUserId());
                accountId = account.map(Account::getId).orElse(null);
                cashOperation.setAccountId(accountId);
                BigDecimal amount = account.map(account1 -> account1.getBalance()).orElse(BigDecimal.ZERO);
                result = CashOperationResultDTO.builder()
                        .operationId(operationDTO.getId())
                        .message("Operation " +operationDTO.getOperationType().name()+ " successful")
                        .status(OperationStatus.SENT)
                        .newBalance(amount.add(cashOperation.getAmount()))
                        .build();
            }

            case EXCHANGE -> {
                result = ExchangeOperationResultDTO.builder()
                        .operationId(operationDTO.getId())
                        .message("Operation " +operationDTO.getOperationType().name()+ " successful")
                        .status(OperationStatus.SENT)
                        .build();
            }
            case TRANSFER -> {
                result = TransferOperationResultDTO.builder()
                        .operationId(operationDTO.getId())
                        .message("Operation " +operationDTO.getOperationType().name()+ " successful")
                        .status(OperationStatus.SENT)
                        .build();
            }
        }

        saveOutboxEvent(accountId, operationDTO);

        return Optional.of(result);
    }


    private void saveOutboxEvent(Integer accountId, GenericOperationDTO operationDTO) {
        try {
            OutboxEvent event = OutboxEvent.builder()
                    .aggregateId(operationDTO.getId())
                    .accountId(accountId)
                    .aggregateType("ACCOUNT")
                    .operationType(operationDTO.getOperationType())
                    .payload(objectMapper.writeValueAsString(operationDTO))
                    .build();
            eventRepository.save(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize payload", e);
        }
    }
}

