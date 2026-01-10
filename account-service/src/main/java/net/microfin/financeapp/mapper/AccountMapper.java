package net.microfin.financeapp.mapper;

import net.microfin.financeapp.dto.AccountDTO;
import net.microfin.financeapp.jooq.tables.records.AccountsRecord;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountMapper {
    public AccountsRecord toRecord(AccountDTO accountDTO) {
        AccountsRecord accountsRecord = new AccountsRecord();
        accountsRecord
                .setAccountId(accountDTO.getId())
                .setBalance(accountDTO.getBalance())
                .setCreatedAt(accountDTO.getCreatedAt())
                .setUpdatedAt(accountDTO.getUpdatedAt())
                .setUserId(accountDTO.getUserId())
                .setCurrencyCode(accountDTO.getCurrencyCode())
                .setIsActive(accountDTO.getActive());
        return accountsRecord;
    }

    public AccountDTO toDTO(AccountsRecord accountsRecord) {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setActive(accountsRecord.getIsActive());
        accountDTO.setBalance(accountsRecord.getBalance());
        accountDTO.setCreatedAt(accountsRecord.getCreatedAt());
        accountDTO.setUpdatedAt(accountsRecord.getUpdatedAt());
        accountDTO.setId(accountsRecord.getAccountId());
        accountDTO.setId(accountsRecord.getAccountId());
        return accountDTO;
    }

    public List<AccountDTO> toDTOList(List<AccountsRecord> accountsRecords) {
        return accountsRecords.stream().map(this::toDTO).toList();
    }

}
