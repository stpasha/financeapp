package net.microfin.financeapp.web.controller;

import net.microfin.financeapp.dto.CashOperationDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @PostMapping("{userId}")
    public String performCashOperation(@ModelAttribute CashOperationDTO operationDTO) {
        return "redirect:profile";
    }
}
