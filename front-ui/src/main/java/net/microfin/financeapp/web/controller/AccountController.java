package net.microfin.financeapp.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.AccountDTO;
import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.ExchangeOperationDTO;
import net.microfin.financeapp.dto.OperationResult;
import net.microfin.financeapp.dto.TransferOperationDTO;
import net.microfin.financeapp.service.AccountService;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/{userId}/cash")
    public String performCashOperation(@ModelAttribute("cashDTO") @Valid CashOperationDTO cashDTO,
                                 @PathVariable("userId") Integer userId,
                                 BindingResult bindingResult) {
        return handleErrors(bindingResult, "cashErrors").map(String::toString).orElseGet(() ->{
            OperationResult operationResult = accountService.createCashOperation(cashDTO);
            if (OperationStatus.FAILED.equals(operationResult.getStatus()) || OperationStatus.RETRYABLE.equals(operationResult.getStatus())) {
                return "redirect:/profile?err=" + operationResult.getMessage();
            }
            return "redirect:/profile?info=" + operationResult.getMessage();
        });
    }

    @PostMapping("/{userId}/exchange")
    public String performExchangeOperation(@ModelAttribute("exchangeDTO") @Valid ExchangeOperationDTO exchangeOperationDTO,
                                       @PathVariable("userId") Integer userId,
                                       BindingResult bindingResult) {
        return handleErrors(bindingResult, "transferErrors").map(String::toString).orElseGet(() ->{
            OperationResult operationResult = accountService.createExchangeOperation(exchangeOperationDTO);
            if (OperationStatus.FAILED.equals(operationResult.getStatus()) || OperationStatus.RETRYABLE.equals(operationResult.getStatus())) {
                return "redirect:/profile?err=" + operationResult.getMessage();
            }
            return "redirect:/profile?info=" + operationResult.getMessage();
        });
    }

    @PostMapping("/{userId}/transfer")
    public String performTransferOperation(@ModelAttribute("transferDTO") @Valid TransferOperationDTO transferOperationDTO,
                                           @PathVariable("userId") Integer userId,
                                           BindingResult bindingResult) {
        return handleErrors(bindingResult, "transferOtherErrors").map(String::toString).orElseGet(() ->{
            OperationResult operationResult = accountService.createTransferOperation(transferOperationDTO);
            if (OperationStatus.FAILED.equals(operationResult.getStatus()) || OperationStatus.RETRYABLE.equals(operationResult.getStatus())) {
                return "redirect:/profile?err=" + operationResult.getMessage();
            }
            return "redirect:/profile?info=" + operationResult.getMessage();
        });
    }

    @GetMapping("/{userId}")
    @ResponseBody
    public List<AccountDTO> getAccountsByUserId(@PathVariable("userId") Integer userId) {
        return accountService.getAccountsByUser(userId);
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
