package net.microfin.financeapp.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.CurrencyDTO;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UserDTO;
import net.microfin.financeapp.service.AccountService;
import net.microfin.financeapp.service.DictionaryService;
import net.microfin.financeapp.service.NotificationService;
import net.microfin.financeapp.service.UserService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final DictionaryService dictionaryService;
    private final AccountService accountService;
    private final NotificationService notificationService;

    @GetMapping("/")
    public String rootRedirect(Principal principal) {
        if (principal == null) {
            return "main";
        } else {
            return "redirect:profile";
        }
    }

    @GetMapping("/profile")
    public String profile(Principal principal,
                          Model model,
                          @RequestParam(required = false) String info,
                          @RequestParam(required = false) String err,
                          @RequestParam(required = false) String passwordErrors,
                          @RequestParam(required = false) String userAccountsErrors,
                          @RequestParam(required = false) String transferErrors,
                          @RequestParam(required = false) String cashErrors,
                          @RequestParam(required = false) String transferOtherErrors) {
        if (principal instanceof OAuth2AuthenticationToken token) {
            UserDTO userDTO = userService.queryUserInfo(token.getPrincipal().getAttribute("preferred_username"));
            model.addAttribute("user", userDTO);
            model.addAttribute("userAccount", accountService.getAccountsByUser(userDTO.getId()));
            model.addAttribute("passwordDTO", PasswordDTO.builder().id(userDTO.getId()).build());
            model.addAttribute("info", info);
            model.addAttribute("currencies", dictionaryService.getCurrencies());
            model.addAttribute("passwordErrors", passwordErrors);
            model.addAttribute("userAccountsErrors", userAccountsErrors);
            model.addAttribute("err", err);
            model.addAttribute("cashErrors", cashErrors);
            model.addAttribute("targetUsers", userService.queryTargeUsers());
            model.addAttribute("transferErrors", transferErrors);
            model.addAttribute("transferOtherErrors", transferOtherErrors);
            model.addAttribute("notifications", notificationService.listNotifications(userDTO.getId()));
            return "profile";
        }
        return "redirect:/login";

    }

    @GetMapping("/rates")
    @ResponseBody
    public List<CurrencyDTO> currencyDTOList() {
        return dictionaryService.getCurrencies();
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
