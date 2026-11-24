package com.bankease.controller;

import com.bankease.entity.User;
import com.bankease.repository.UserRepository;
import com.bankease.repository.BankAccountRepository;
import com.bankease.service.BankingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class BankingController {
    private final UserRepository users; private final BankingService banking; private final BankAccountRepository accounts;
    public BankingController(UserRepository users,BankingService banking,BankAccountRepository accounts){this.users=users;this.banking=banking;this.accounts=accounts;}
    private User current(Authentication auth){return users.findByEmail(auth.getName()).orElseThrow();}

    @PostMapping("/account/open") public String open(Authentication auth,
            @RequestParam String accountType, @RequestParam String mobileNumber,
            @RequestParam LocalDate dateOfBirth, @RequestParam String address,
            @RequestParam(required=false) String nomineeName,
            @RequestParam(required=false) String nomineeRelationship,
            @RequestParam BigDecimal initialDeposit, RedirectAttributes ra){
        try {
            banking.openAccount(current(auth),accountType,mobileNumber,dateOfBirth,address,nomineeName,nomineeRelationship,initialDeposit);
            ra.addFlashAttribute("message","Your bank account has been created successfully.");
            return "redirect:/account/details";
        } catch (Exception e) {
            ra.addFlashAttribute("error",e.getMessage());
            return "redirect:/account/open";
        }
    }
    @PostMapping("/account/deposit") public String deposit(Authentication auth,@RequestParam Long accountId,@RequestParam BigDecimal amount,RedirectAttributes ra){
        try{banking.deposit(current(auth),accountId,amount);ra.addFlashAttribute("message","Deposit completed.");}
        catch(Exception e){ra.addFlashAttribute("error",e.getMessage());} return "redirect:/account/deposit";
    }
    @PostMapping("/account/withdraw") public String withdraw(Authentication auth,@RequestParam Long accountId,@RequestParam BigDecimal amount,RedirectAttributes ra){
        try{banking.withdraw(current(auth),accountId,amount);ra.addFlashAttribute("message","Withdrawal completed.");}
        catch(Exception e){ra.addFlashAttribute("error",e.getMessage());} return "redirect:/account/withdraw";
    }
    @PostMapping("/account/transfer") public String transfer(Authentication auth,@RequestParam Long accountId,@RequestParam String recipientAccount,@RequestParam BigDecimal amount,RedirectAttributes ra){
        try{banking.transfer(current(auth),accountId,recipientAccount,amount);ra.addFlashAttribute("message","Transfer completed.");}
        catch(Exception e){ra.addFlashAttribute("error",e.getMessage());} return "redirect:/account/transfer";
    }
    @PostMapping("/admin/account/status") public String status(@RequestParam Long accountId,@RequestParam String status,RedirectAttributes ra){
        var a=accounts.findById(accountId).orElse(null);
        if(a == null){
            ra.addFlashAttribute("error","Account not found.");
        } else if(status.equals("ACTIVE") || status.equals("FROZEN")){
            a.setStatus(status);
            accounts.save(a);
            ra.addFlashAttribute("message", status.equals("FROZEN") ? "Account frozen successfully." : "Account activated successfully.");
        } else {
            ra.addFlashAttribute("error","Invalid account status.");
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/account/delete") public String deleteAccount(@RequestParam Long accountId,RedirectAttributes ra){
        var account = accounts.findById(accountId).orElse(null);
        if(account == null){
            ra.addFlashAttribute("error","Account not found.");
            return "redirect:/admin/dashboard";
        }
        // Remove related transaction rows first to satisfy the database foreign key.
        // This permanently deletes this bank account and its transaction history.
        banking.deleteAccountPermanently(account);
        ra.addFlashAttribute("message","Bank account and its transaction history were permanently deleted.");
        return "redirect:/admin/dashboard";
    }
}
