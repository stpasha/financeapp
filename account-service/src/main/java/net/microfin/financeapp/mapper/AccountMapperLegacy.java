package net.microfin.financeapp.mapper;

import net.microfin.financeapp.domain.Account;
import net.microfin.financeapp.domain.User;
import net.microfin.financeapp.dto.AccountDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AccountMapperLegacy {

    @Mapping(target = "user", source = "userId")
    @Mapping(target = "currencyCode", source = "currencyCode")
    Account toEntity(AccountDTO dto);
    @Mapping(target = "userId", source = "user")
    @Mapping(target = "currencyCode", source = "currencyCode")
    AccountDTO toDto(Account account);

    List<AccountDTO> toDtoList(List<Account> accounts);

    default UUID map(User user) {
        return user != null ? user.getId() : null;
    }

    default User map(UUID userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}
