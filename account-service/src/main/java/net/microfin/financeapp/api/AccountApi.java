package net.microfin.financeapp.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.service.AccountService;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@PreAuthorize("hasRole('zbank.user')")
public class AccountApi {

    private final AccountService accountService;
    private final ObjectMapper objectMapper;


    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        return accountService.createAccount(accountDTO)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.CREATED))
                .orElseThrow(() -> new EntityNotFoundException("Не удалось создать счёт " + accountDTO));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountDTO>> getAccountsByUser(@PathVariable("userId") Integer userId) {
        return ResponseEntity.ok(accountService.getAccountsByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable("id") Integer id) {
        return accountService.getAccount(id).map(ResponseEntity::ok).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity disable(@PathVariable("id") Integer id) {
        accountService.disable(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/operation")
    public ResponseEntity<OperationResult> performOperation(@RequestBody JsonNode json) {
        GenericOperationDTO genericOperationDTO = null;
        String operationType = json.get("operationType").asText("");

        switch (OperationType.valueOf(OperationType.class, operationType)) {
            case OperationType.CASH_DEPOSIT, OperationType.CASH_WITHDRAWAL -> {
                genericOperationDTO = fromJson(json.asText(), CashOperationDTO.class);
            }
            case EXCHANGE -> {
                genericOperationDTO = fromJson(json.asText(), ExchangeOperationDTO.class);
            }
            case TRANSFER -> {
                genericOperationDTO = fromJson(json.asText(), TransferOperationDTO.class);
            }
            default -> throw new RuntimeException("Operation type not recognized");
        }
        return ResponseEntity.ok(accountService.processOperation(genericOperationDTO).orElseGet(() -> EmptyOperationResult.builder().status(OperationStatus.FAILED).message("Unnable to process").build()));
    }

    private <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot deserialize payload", e);
        }
    }


}
