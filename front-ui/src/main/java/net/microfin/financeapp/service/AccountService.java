package net.microfin.financeapp.service;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.client.AccountClient;
import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.OperationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountClient accountClient;

    public OperationResult createCashOperation(CashOperationDTO cashOperationDTO) {
        ResponseEntity<OperationResult> responseEntity = accountClient.cashOperation(cashOperationDTO);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        throw new RuntimeException("Unable to update user");
    }

    private Optional<String> handleErrors(BindingResult bindingResult, String errAttribute) {
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce((m1, m2) -> m1 + ", " + m2)
                    .orElse("Validation error");

            return Optional.of("redirect:/profile?" + errAttribute + "=" + UriUtils.encode(errorMessages, StandardCharsets.UTF_8));
        }
        return Optional.empty();
    }
}
