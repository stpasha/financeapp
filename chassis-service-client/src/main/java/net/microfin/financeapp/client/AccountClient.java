package net.microfin.financeapp.client;

import net.microfin.financeapp.dto.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@FeignClient(name = "account-service", url = "http://gateway-service:8082")
public interface AccountClient {
    @GetMapping("/api/accounts/user/{id}")
    List<AccountDTO> getAccountsByUser(@PathVariable(name = "id") Integer userId);
    @PostMapping("/api/accounts")
    AccountDTO createAccount(@RequestBody AccountDTO accountDTO);
    @PutMapping("/api/accounts/{id}/disable")
    ResponseEntity<Void> disableAccount(@PathVariable(name = "id") Integer accountId);
}
