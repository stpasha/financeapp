package net.microfin.financeapp.mapper;

import net.microfin.financeapp.domain.Account;
import net.microfin.financeapp.dto.AccountDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface AccountMapper {
    Account toEntity(AccountDTO accountDTO);
    AccountDTO toDto(Account account);
    List<AccountDTO> toDtoList(List<Account> accounts);
}
