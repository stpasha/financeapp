package net.microfin.financeapp.client;

import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.ExchangeOperationDTO;
import net.microfin.financeapp.dto.TransferOperationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuditClient {
    @GetMapping("/api/audit")
    ResponseEntity<Boolean> check(@RequestBody ExchangeOperationDTO cashOperationDTO);

    @GetMapping("/api/audit")
    ResponseEntity<Boolean> check(@RequestBody CashOperationDTO cashOperationDTO);

    @GetMapping("/api/audit")
    ResponseEntity<Boolean> check(@RequestBody TransferOperationDTO cashOperationDTO);

}
