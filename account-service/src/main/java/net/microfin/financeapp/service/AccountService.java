package net.microfin.financeapp.service;

import net.microfin.financeapp.dto.AccountDTO;
import net.microfin.financeapp.dto.UserDTO;

import java.util.List;

public interface AccountService {
    List<AccountDTO> getAccountsByUser(UserDTO userDTO);
    AccountDTO createAccount(AccountDTO accountDTO);
    void disableAccount(AccountDTO accountDTO);
}
