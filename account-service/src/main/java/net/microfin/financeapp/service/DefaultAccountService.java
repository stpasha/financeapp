package net.microfin.financeapp.service;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.AccountDTO;
import net.microfin.financeapp.mapper.AccountMapper;
import net.microfin.financeapp.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultAccountService implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public List<AccountDTO> getAccountsByUserId(Integer userId) {
        return accountMapper.toDtoList(accountRepository.findAccountsByUserId(userId));
    }

    @Override
    public Optional<AccountDTO> createAccount(AccountDTO accountDTO) {
        return Optional.of(accountRepository.save(accountMapper.toEntity(accountDTO)))
                .map(account -> accountMapper.toDto(account));
    }

    @Override
    public void disable(Integer id) {
        accountRepository.disableAccount(id);
    }
}
