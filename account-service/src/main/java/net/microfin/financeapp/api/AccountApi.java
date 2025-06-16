package net.microfin.financeapp.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.config.ExceptionsProperties;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.service.AccountService;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Slf4j
public class AccountApi {

    private final AccountService accountService;
    private final ObjectMapper objectMapper;
    private final ExceptionsProperties exceptionsProperties;


    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        return accountService.createAccount(accountDTO)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.CREATED))
                .orElseThrow(() -> new EntityNotFoundException(exceptionsProperties.getMakeAccFailure() + accountDTO));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountDTO>> getAccountsByUser(@PathVariable("userId") Integer userId) {
        log.info("HELLO: " + exceptionsProperties.getMakeAccFailure());
        return ResponseEntity.ok(accountService.getAccountsByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable("id") Integer id) {
        return accountService.getAccount(id).map(ResponseEntity::ok).orElseThrow(() -> new RuntimeException(exceptionsProperties.getSearchAccFailure()));
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
                genericOperationDTO = fromJson(json.toString(), CashOperationDTO.class);
            }
            case EXCHANGE -> {
                genericOperationDTO = fromJson(json.toString(), ExchangeOperationDTO.class);
            }
            case TRANSFER -> {
                genericOperationDTO = fromJson(json.toString(), TransferOperationDTO.class);
            }
            default -> throw new RuntimeException(exceptionsProperties.getOperationFailure());
        }
        return ResponseEntity.ok(accountService.processOperation(genericOperationDTO).orElseGet(() -> EmptyOperationResult.builder().status(OperationStatus.FAILED).message("Unnable to process").build()));
    }

    private <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(exceptionsProperties.getDeserFailure(), e);
        }
    }
}
