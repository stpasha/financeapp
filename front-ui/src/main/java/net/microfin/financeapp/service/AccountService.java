package net.microfin.financeapp.service;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.client.AccountClient;
import net.microfin.financeapp.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountClient accountClient;

    public OperationResult createCashOperation(CashOperationDTO cashOperationDTO) {
        ResponseEntity<CashOperationResultDTO> responseEntity = accountClient.cashOperation(cashOperationDTO);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        throw new RuntimeException("Unable to perform cash operation");
    }

    public OperationResult createExchangeOperation(ExchangeOperationDTO exchangeOperationDTO) {
        ResponseEntity<CashOperationResultDTO> responseEntity = accountClient.exchangeOperation(exchangeOperationDTO);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        throw new RuntimeException("Unable to perform exchange operation");
    }

    public OperationResult createTransferOperation(TransferOperationDTO transferOperationDTO) {
        ResponseEntity<CashOperationResultDTO> responseEntity = accountClient.transferOperation(transferOperationDTO);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        throw new RuntimeException("Unable to perform transfer operation");
    }

    public List<AccountDTO> getAccountsByUser(Integer userId) {
        ResponseEntity<List<AccountDTO>> responseEntity = accountClient.getAccountsByUser(userId);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        throw new RuntimeException("Unable to get account info");
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
