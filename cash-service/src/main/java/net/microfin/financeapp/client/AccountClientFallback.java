package net.microfin.financeapp.client;

import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class AccountClientFallback implements AccountClientImpl {

    @Override
    public ResponseEntity<List<AccountDTO>> getAccountsByUser(UUID userId) {
        log.warn("Fallback: getAccountsByUser failed for userId={}", userId);
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<Void> disable(UUID id) {
        log.warn("Fallback: disable failed for id={}", id);
        return ResponseEntity.status(503).build();
    }

    @Override
    public AccountDTO createAccount(AccountDTO accountDTO) {
        return null;
    }

    @Override
    public ResponseEntity<Void> disableAccount(UUID accountId) {
        return null;
    }

    @Override
    public ResponseEntity<ExchangeOperationResultDTO> exchangeOperation(ExchangeOperationDTO dto) {
        return ResponseEntity.status(503).body(ExchangeOperationResultDTO.builder().message("Fallback: exchange failed").status(OperationStatus.FAILED).build());
    }

    @Override
    public ResponseEntity<CashOperationResultDTO> cashOperation(CashOperationDTO dto) {
        return ResponseEntity.status(503).body(CashOperationResultDTO.builder().message("Fallback: cash failed").status(OperationStatus.FAILED).build());
    }


    @Override
    public ResponseEntity<TransferOperationResultDTO> transferOperation(TransferOperationDTO dto) {
        return ResponseEntity.status(503).body(TransferOperationResultDTO.builder().message("Fallback: transfer failed").status(OperationStatus.FAILED).build());
    }

    @Override
    public ResponseEntity<AccountDTO> getAccount(UUID id) {
        return ResponseEntity.status(503).body(null);
    }

    @Override
    public ResponseEntity<UserDTO> create(UserDTO userDTO) {
        log.warn("Fallback triggered for create user due to service unavailability");
        return ResponseEntity.status(503).build();
    }

    @Override
    public ResponseEntity<UserDTO> update(UpdateUserDTO userDTO) {
        log.warn("Fallback triggered for update user due to service unavailability");
        return ResponseEntity.status(503).build();
    }

    @Override
    public ResponseEntity<UserDTO> updatePassword(PasswordDTO userDTO) {
        log.warn("Fallback triggered for updatePassword due to service unavailability");
        return ResponseEntity.status(503).build();
    }

    @Override
    public ResponseEntity<UserDTO> getUserByName(String username) {
        log.warn("Fallback triggered for getUserByName with username={} due to service unavailability", username);
        return ResponseEntity.status(503).build();
    }

    @Override
    public ResponseEntity<List<UserDTO>> getUsers() {
        log.warn("Fallback triggered for getUsers due to service unavailability");
        return ResponseEntity.ok(Collections.emptyList());
    }
}
