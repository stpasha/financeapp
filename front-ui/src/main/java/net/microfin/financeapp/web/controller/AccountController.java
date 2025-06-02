package net.microfin.financeapp.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private AccountService accountService;

    @PostMapping("/{userId}/cash")
    public String performCashOperation(@ModelAttribute("cashDTO") @Valid CashOperationDTO cashDTO,
                                 @PathVariable("userId") Integer userId,
                                 BindingResult bindingResult) {
        return handleErrors(bindingResult, "passwordErrors").map(String::toString).orElseGet(() ->{
            accountService.createCashOperation(cashDTO);
            return "redirect:/profile";
        });
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
