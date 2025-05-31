package net.microfin.financeapp.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UpdateUserDTO;
import net.microfin.financeapp.dto.UserDTO;
import net.microfin.financeapp.service.UserService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/password")
    public String changePassword(@ModelAttribute("passwordDTO") @Valid PasswordDTO passwordDTO,
                                 BindingResult bindingResult,
                                 Principal principal,
                                 Model model) {
        if (!Objects.equals(passwordDTO.getPassword(), passwordDTO.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Пароли не совпадают");
        }
        return handleErrors(bindingResult, principal, model,"passwordErrors").map(String::toString).orElseGet(() ->{
            userService.changePassword(passwordDTO);
            return "redirect:/profile";
        });
    }

    @PutMapping
    public String updateUser(@ModelAttribute("user") @Valid UpdateUserDTO userDTO,
                                 BindingResult bindingResult,  Model model, Principal principal) {
        return handleErrors(bindingResult, principal, model, "userAccountsErrors").map(String::toString).orElseGet(() ->{
            userService.updateUser(userDTO);
            return "redirect:/profile";
        });
    }

    private Optional<String> handleErrors(BindingResult bindingResult,
                                          Principal principal,
                                          Model model, String errAttribute) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(errAttribute, bindingResult.getFieldErrors());
            if (principal instanceof OAuth2AuthenticationToken token) {
                UserDTO userInfo = userService.queryUserInfo(token.getPrincipal().getAttribute("preferred_username"));
                model.addAttribute("user", userInfo);
                model.addAttribute("passwordDTO", PasswordDTO.builder().id(userInfo.getId()).build());
                return Optional.of("profile");
            }
            return Optional.of("redirect:/profile");
        }
        return Optional.empty();
    }
}
