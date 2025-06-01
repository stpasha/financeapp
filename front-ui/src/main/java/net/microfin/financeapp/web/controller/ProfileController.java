package net.microfin.financeapp.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UserDTO;
import net.microfin.financeapp.service.DictionaryService;
import net.microfin.financeapp.service.UserService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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
public class ProfileController {

    private final UserService userService;
    private final DictionaryService dictionaryService;

    @GetMapping("/")
    public String rootRedirect(Principal principal) {
        if (principal == null) {
            return "main";
        } else {
            return "redirect:profile";
        }
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        if (principal instanceof OAuth2AuthenticationToken token) {
            UserDTO userDTO = userService.queryUserInfo(token.getPrincipal().getAttribute("preferred_username"));
            model.addAttribute("user", userDTO);
            model.addAttribute("passwordDTO", PasswordDTO.builder().id(userDTO.getId()).build());
            model.addAttribute("currencies", dictionaryService.getCurrencies());
            return "profile";
        }
        return "redirect:/login";

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
            return "signup";
        }
        userService.create(user);
        return "redirect:/login";
    }

    @GetMapping("/access-denied")
    public ModelAndView showAccessDeniedPage() {
        return new ModelAndView("access-denied");
    }

}
