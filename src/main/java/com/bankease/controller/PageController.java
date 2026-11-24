package com.bankease.controller;

import com.bankease.entity.User;
import com.bankease.repository.UserRepository;
import com.bankease.repository.BankAccountRepository;
import com.bankease.service.BankingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    private final UserRepository users;
    private final BankingService banking;
    private final BankAccountRepository accounts;
    public PageController(UserRepository users, BankingService banking, BankAccountRepository accounts){this.users=users;this.banking=banking;this.accounts=accounts;}

    @GetMapping("/") public String home(){return "redirect:/dashboard";}
    @GetMapping("/dashboard") public String dashboard(Authentication auth, Model model){
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")))
            return "redirect:/admin/dashboard";
        User user=users.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user",user);
        model.addAttribute("accounts",banking.accountsFor(user));
        return "dashboard";
    }
    @GetMapping("/account/open") public String openAccountForm(Authentication auth, Model model){
        model.addAttribute("user", users.findByEmail(auth.getName()).orElseThrow());
        return "open-account";
    }
    @GetMapping("/account/details") public String accountDetails(Authentication auth, Model model){
        User user=users.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user",user); model.addAttribute("accounts",banking.accountsFor(user));
        return "account-details";
    }
    @GetMapping("/account/deposit") public String depositPage(Authentication auth, Model model){
        addAccountPageData(auth,model); return "deposit";
    }
    @GetMapping("/account/withdraw") public String withdrawPage(Authentication auth, Model model){
        addAccountPageData(auth,model); return "withdraw";
    }
    @GetMapping("/account/transfer") public String transferPage(Authentication auth, Model model){
        addAccountPageData(auth,model); return "transfer";
    }
    @GetMapping("/account/transactions") public String transactionsPage(Authentication auth, Model model){
        User user=users.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user",user); model.addAttribute("accounts",banking.accountsFor(user));
        model.addAttribute("transactions",banking.transactionsFor(user));
        return "transactions";
    }
    private void addAccountPageData(Authentication auth, Model model){
        User user=users.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user",user); model.addAttribute("accounts",banking.accountsFor(user));
    }
    @GetMapping("/admin/dashboard") public String admin(Model model){
        model.addAttribute("users",users.findAll());
        model.addAttribute("accounts",accounts.findAll());
        return "admin";
    }
}
