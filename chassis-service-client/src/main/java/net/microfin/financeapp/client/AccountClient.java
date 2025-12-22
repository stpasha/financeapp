package net.microfin.financeapp.client;

import net.microfin.financeapp.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

public interface AccountClient {
    @GetMapping("/api/account/user/{userId}")
    ResponseEntity<List<AccountDTO>> getAccountsByUser(@PathVariable("userId") UUID userId);

    @PutMapping("/api/account/{id}/disable")
    ResponseEntity<Void> disable(@PathVariable("id") UUID id);

    @PostMapping("/api/accounts")
    AccountDTO createAccount(@RequestBody AccountDTO accountDTO);

    @PutMapping("/api/accounts/{id}/disable")
    ResponseEntity<Void> disableAccount(@PathVariable(name = "id") UUID accountId);

    @PostMapping("/api/account/operation")
    ResponseEntity<ExchangeOperationResultDTO> exchangeOperation(@RequestBody ExchangeOperationDTO exchangeOperationDTO);

    @GetMapping("/api/account/{id}")
    ResponseEntity<AccountDTO> getAccount(@PathVariable("id") UUID id);

    @PostMapping("/api/account/operation")
    ResponseEntity<TransferOperationResultDTO> transferOperation(@RequestBody TransferOperationDTO transferOperationDTO);

    @PostMapping("/api/account/operation")
    ResponseEntity<CashOperationResultDTO> cashOperation(@RequestBody CashOperationDTO cashOperationDTO);

    @PostMapping("/api/user")
    ResponseEntity<UserDTO> create(@RequestBody UserDTO userDTO);

    @PutMapping("/api/user")
    ResponseEntity<UserDTO> update(@RequestBody UpdateUserDTO userDTO);

    @PutMapping("/api/user/password")
    ResponseEntity<UserDTO> updatePassword(@RequestBody PasswordDTO userDTO);

    @GetMapping("/api/user/{username}")
    ResponseEntity<UserDTO> getUserByName(@PathVariable(name = "username") String username);

    @GetMapping("/api/user")
    ResponseEntity<List<UserDTO>> getUsers();
}
