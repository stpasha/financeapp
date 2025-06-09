package net.microfin.financeapp.client;

import net.microfin.financeapp.config.FeignConfig;
import net.microfin.financeapp.dto.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "rule-client", url = "http://gateway-service:8082",  configuration = FeignConfig.class)
public interface RuleClient {

    @GetMapping("/account/{id}")
    ResponseEntity<AccountDTO> getAccount(@PathVariable("id") Integer id);
}
