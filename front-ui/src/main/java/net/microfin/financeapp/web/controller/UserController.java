package net.microfin.financeapp.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/password")
    public String changePassword(@ModelAttribute @Valid PasswordDTO passwordDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "profile"; // или отдельная форма смены пароля
        }
        if (!Objects.equals(passwordDTO.getPassword(), passwordDTO.getConfirmPassword())) {
            return "redirect:/profile";
        }
       userService.changePassword(passwordDTO);
       return "redirect:/profile";
    }
}
