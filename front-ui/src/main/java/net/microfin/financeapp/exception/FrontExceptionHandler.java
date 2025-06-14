package net.microfin.financeapp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@ControllerAdvice
@Slf4j
public class FrontExceptionHandler extends GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidation(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException occured", ex);
        BindingResult bindingResult = ex.getBindingResult();
        String errAttribute = getErrAttribute(ex, bindingResult);

        String errorMessages = bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((m1, m2) -> m1 + ", " + m2)
                .orElse("Validation error");

        return "redirect:/profile?" + errAttribute + "=" + UriUtils.encode(errorMessages, StandardCharsets.UTF_8);
    }

    private static String getErrAttribute(MethodArgumentNotValidException ex, BindingResult bindingResult) {
        String check = bindingResult.getTarget() != null ? ex.getBindingResult().getTarget().getClass().getSimpleName() : "";
        String errAttribute = "generalErrors";
        switch (check) {
            case "ExchangeOperationDTO" -> {
                errAttribute = "transferErrors";
            }
            case "TransferOperationDTO" -> {
                errAttribute = "transferOtherErrors";
            }
            case "CashOperationDTO" -> {
                errAttribute = "cashErrors";
            }
            case "PasswordDTO" -> {
                errAttribute = "passwordErrors";
            }
            case "UpdateUserDTO" -> {
                errAttribute = "userAccountsErrors";
            }

        }
        return errAttribute;
    }

}