package com.bankease.service;

import com.bankease.entity.*;
import com.bankease.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class BankingService {
    private final BankAccountRepository accounts;
    private final BankTransactionRepository transactions;

    public BankingService(BankAccountRepository accounts, BankTransactionRepository transactions) {
        this.accounts=accounts; this.transactions=transactions;
    }

    public List<BankAccount> accountsFor(User user){ return accounts.findByCustomer(user); }
    public List<BankTransaction> transactionsFor(User user){ return transactions.findByAccountInOrderByCreatedAtDesc(accountsFor(user)); }

    @Transactional
    public void deleteAccountPermanently(BankAccount account) {
        transactions.deleteByAccount(account);
        accounts.delete(account);
    }

    @Transactional
    public BankAccount openAccount(User user, String accountType, String mobileNumber,
            LocalDate dateOfBirth, String address, String nomineeName,
            String nomineeRelationship, BigDecimal initialDeposit) {
        if (accountType == null || !(accountType.equals("SAVINGS") || accountType.equals("CURRENT")))
            throw new IllegalArgumentException("Select a valid account type.");
        if (mobileNumber == null || !mobileNumber.matches("[0-9]{10}"))
            throw new IllegalArgumentException("Enter a valid 10-digit mobile number.");
        if (dateOfBirth == null || !dateOfBirth.isBefore(LocalDate.now().minusYears(18).plusDays(1)))
            throw new IllegalArgumentException("Customer must be at least 18 years old.");
        if (address == null || address.isBlank()) throw new IllegalArgumentException("Enter your residential address.");
        if (initialDeposit == null || initialDeposit.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Initial deposit cannot be negative.");

        BankAccount a = new BankAccount();
        a.setAccountNumber("BE" + UUID.randomUUID().toString().replace("-", "").substring(0,10).toUpperCase());
        a.setCustomer(user);
        a.setAccountType(accountType);
        a.setMobileNumber(mobileNumber.trim());
        a.setDateOfBirth(dateOfBirth);
        a.setAddress(address.trim());
        a.setNomineeName(nomineeName == null || nomineeName.isBlank() ? null : nomineeName.trim());
        a.setNomineeRelationship(nomineeRelationship == null || nomineeRelationship.isBlank() ? null : nomineeRelationship.trim());
        a.setBalance(initialDeposit);
        BankAccount saved = accounts.save(a);
        if (initialDeposit.compareTo(BigDecimal.ZERO) > 0)
            transactions.save(new BankTransaction("INITIAL DEPOSIT", initialDeposit, "Initial account deposit", saved));
        return saved;
    }

    @Transactional
    public void deposit(User user, Long accountId, BigDecimal amount) {
        BankAccount a = ownedAccount(user, accountId); validateAmount(amount);
        a.setBalance(a.getBalance().add(amount));
        transactions.save(new BankTransaction("DEPOSIT", amount, "Cash deposit", a));
    }

    @Transactional
    public void withdraw(User user, Long accountId, BigDecimal amount) {
        BankAccount a = ownedAccount(user, accountId); validateAmount(amount);
        if (a.getBalance().compareTo(amount) < 0) throw new IllegalArgumentException("Insufficient balance.");
        a.setBalance(a.getBalance().subtract(amount));
        transactions.save(new BankTransaction("WITHDRAWAL", amount, "Cash withdrawal", a));
    }

    @Transactional
    public void transfer(User user, Long fromId, String toNumber, BigDecimal amount) {
        BankAccount from = ownedAccount(user, fromId);
        BankAccount to = accounts.findByAccountNumber(toNumber == null ? "" : toNumber.trim())
            .orElseThrow(() -> new IllegalArgumentException("Recipient account was not found."));
        if (from.getId().equals(to.getId())) throw new IllegalArgumentException("You cannot transfer to the same account.");
        validateAmount(amount);
        if (!"ACTIVE".equals(to.getStatus()) || !"ACTIVE".equals(from.getStatus()))
            throw new IllegalArgumentException("Both accounts must be active.");
        if (from.getBalance().compareTo(amount) < 0) throw new IllegalArgumentException("Insufficient balance.");
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        transactions.save(new BankTransaction("TRANSFER OUT", amount, "Transfer to " + to.getAccountNumber(), from));
        transactions.save(new BankTransaction("TRANSFER IN", amount, "Transfer from " + from.getAccountNumber(), to));
    }

    private BankAccount ownedAccount(User user, Long id) {
        BankAccount a = accounts.findById(id).orElseThrow(() -> new IllegalArgumentException("Account not found."));
        if (!a.getCustomer().getId().equals(user.getId())) throw new IllegalArgumentException("You cannot access this account.");
        if (!"ACTIVE".equals(a.getStatus())) throw new IllegalArgumentException("This account is not active.");
        return a;
    }
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Enter an amount greater than zero.");
    }
}
