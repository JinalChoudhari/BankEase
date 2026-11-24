package com.bankease.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_transactions")
public class BankTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false) private String type;
    @Column(nullable=false, precision=15, scale=2) private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne(optional=false) private BankAccount account;

    public BankTransaction() {}
    public BankTransaction(String type, BigDecimal amount, String description, BankAccount account) {
        this.type=type; this.amount=amount; this.description=description; this.account=account;
    }
    public Long getId(){return id;}
    public String getType(){return type;}
    public BigDecimal getAmount(){return amount;}
    public String getDescription(){return description;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public BankAccount getAccount(){return account;}
}
