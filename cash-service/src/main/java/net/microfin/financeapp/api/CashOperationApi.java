package net.microfin.financeapp.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.CashOperationResultDTO;
import net.microfin.financeapp.service.CashOperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/cash")
@RequiredArgsConstructor
@PreAuthorize("hasRole('zbank.user')")
public class CashOperationApi {

    private final CashOperationService cashOperationService;

    @PostMapping("/operation")
    public ResponseEntity<CashOperationResultDTO> performOperation(@Valid @RequestBody CashOperationDTO genericOperationDTO, Principal principal) {
        return cashOperationService.performOperation(genericOperationDTO);
    }
}
