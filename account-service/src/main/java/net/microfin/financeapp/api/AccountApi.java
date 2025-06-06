package net.microfin.financeapp.api;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.AccountDTO;
import net.microfin.financeapp.dto.EmptyOperationResult;
import net.microfin.financeapp.dto.GenericOperationDTO;
import net.microfin.financeapp.dto.OperationResult;
import net.microfin.financeapp.service.AccountService;
import net.microfin.financeapp.util.OperationStatus;
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
    public ResponseEntity<OperationResult> performOperation(@Valid @RequestBody GenericOperationDTO genericOperationDTO) {
        return ResponseEntity.ok(accountService.processOperation(genericOperationDTO).orElseGet(() -> EmptyOperationResult.builder().status(OperationStatus.FAILED).message("Unnable to process").build()));
    }


}
