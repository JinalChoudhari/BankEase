package com.bankease.repository;
import com.bankease.entity.BankTransaction;
import com.bankease.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BankTransactionRepository extends JpaRepository<BankTransaction,Long> {
    List<BankTransaction> findTop30ByAccountInOrderByCreatedAtDesc(List<BankAccount> accounts);
    List<BankTransaction> findByAccountInOrderByCreatedAtDesc(List<BankAccount> accounts);
    void deleteByAccount(BankAccount account);
}
