package net.microfin.financeapp.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.UserDTO;
import net.microfin.financeapp.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
    public String processSignup(@Valid @ModelAttribute UserDTO user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "signup";
        }
        authService.create(user);
        return "redirect:/oauth2/authorization/keycloak";
    }

    @GetMapping("/access-denied")
    public ModelAndView showAccessDeniedPage() {
        return new ModelAndView("access-denied");
    }

}
