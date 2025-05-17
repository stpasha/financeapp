package net.microfin.financeapp.web.controller;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.service.KeycloakUserService;
import net.microfin.financeapp.web.dto.SignupFormDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class FrontController {

    private KeycloakUserService keycloakUserService;

    @GetMapping("/")
    public String rootRedirect(Principal principal) {
        if (principal == null) {
            return "main";
        } else {
            return "redirect:profile";
        }
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        return "profile";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/signup")
    public String showSignup() {
        return "signup"; // thymeleaf шаблон
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute SignupFormDTO user) {
        keycloakUserService.createUser(user);

        // После регистрации — редирект на авторизацию через Keycloak
        return "redirect:/oauth2/authorization/keycloak";
    }



}
