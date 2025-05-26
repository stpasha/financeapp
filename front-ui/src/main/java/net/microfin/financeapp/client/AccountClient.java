package net.microfin.financeapp.client;

import net.microfin.financeapp.config.FeignConfig;
import net.microfin.financeapp.dto.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "account-client", url = "http://gateway-service:8082", configuration = FeignConfig.class)
public interface AccountClient {
    @PostMapping("/account")
    ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO);

    @GetMapping("/account/user/{userId}")
    ResponseEntity<List<AccountDTO>> getAccountsByUser(@PathVariable("userId") Integer userId);

    @PutMapping("/account/{id}/disable")
    ResponseEntity disable(@PathVariable("id") Integer id);

}
