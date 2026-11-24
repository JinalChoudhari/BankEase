package com.bankease.repository;
import com.bankease.entity.BankAccount;
import com.bankease.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface BankAccountRepository extends JpaRepository<BankAccount,Long> {
    List<BankAccount> findByCustomer(User customer);
    Optional<BankAccount> findByAccountNumber(String accountNumber);
}
