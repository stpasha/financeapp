package net.microfin.financeapp.service;

import net.microfin.financeapp.dto.AccountDTO;
import net.microfin.financeapp.dto.GenericOperationDTO;
import net.microfin.financeapp.dto.OperationResult;
import net.microfin.financeapp.dto.UserDTO;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    List<AccountDTO> getAccountsByUserId(Integer userId);
    Optional<AccountDTO> createAccount(AccountDTO accountDTO);
    void disable(Integer id);
    Optional<OperationResult> processOperation(GenericOperationDTO operationDTO);
    Optional<AccountDTO> getAccount(Integer id);
}
