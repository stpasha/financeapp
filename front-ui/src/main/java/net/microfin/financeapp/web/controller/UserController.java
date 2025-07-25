package net.microfin.financeapp.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UpdateUserDTO;
import net.microfin.financeapp.service.DictionaryService;
import net.microfin.financeapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final DictionaryService dictionaryService;


    @PostMapping("/password")
    public String changePassword(@ModelAttribute("passwordDTO") @Valid PasswordDTO passwordDTO,
                                 BindingResult bindingResult) {
        if (!Objects.equals(passwordDTO.getPassword(), passwordDTO.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Пароли не совпадают");
        }
        return handleErrors(bindingResult, "passwordErrors").map(String::toString).orElseGet(() ->{
            userService.changePassword(passwordDTO);
            return "redirect:/profile";
        });
    }

    @PutMapping
    public String updateUser(@ModelAttribute("user") @Valid UpdateUserDTO userDTO,
                                 BindingResult bindingResult) {
        return handleErrors(bindingResult, "userAccountsErrors").map(String::toString).orElseGet(() ->{
            userService.updateUser(userDTO);
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
