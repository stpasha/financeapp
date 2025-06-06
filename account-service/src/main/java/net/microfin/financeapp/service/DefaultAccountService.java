package net.microfin.financeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.domain.Account;
import net.microfin.financeapp.domain.OutboxEvent;
import net.microfin.financeapp.domain.User;
import net.microfin.financeapp.dto.AccountDTO;
import net.microfin.financeapp.dto.CashOperationResultDTO;
import net.microfin.financeapp.dto.GenericOperationDTO;
import net.microfin.financeapp.dto.OperationResult;
import net.microfin.financeapp.mapper.AccountMapper;
import net.microfin.financeapp.repository.AccountRepository;
import net.microfin.financeapp.repository.OutboxEventRepository;
import net.microfin.financeapp.repository.UserRepository;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultAccountService implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final OutboxEventRepository eventRepository;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

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
        switch (operationDTO.getOperationType()) {
            case CASH_DEPOSIT, CASH_WITHDRAWAL -> {
                accountId = accountRepository.findByCurrencyCodeAndUserId(
                       operationDTO.getCurrencyCode(),
                        operationDTO.getUserId()).map(account -> account.getId()).orElse(null);
                operationDTO.setAccountId(accountId);
            }
        }

        saveOutboxEvent(accountId, operationDTO);

        CashOperationResultDTO result = CashOperationResultDTO.builder()
                .operationId(operationDTO.getId())
                .message("Operation successful")
                .status(OperationStatus.SENT.name())
                .newBalance(operationDTO.getAmount())
                .build();

        return Optional.of(result);
    }

    private Account createAccountForUser(GenericOperationDTO operationDTO) {
        User user = userRepository.findById(operationDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = Account.builder()
                .active(true)
                .balance(operationDTO.getAmount())
                .currencyCode(operationDTO.getCurrencyCode())
                .user(user)
                .build();

        return accountRepository.save(account);
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

