package com.bankease.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true) private String accountNumber;
    @Column(nullable=false, precision=15, scale=2) private BigDecimal balance = BigDecimal.ZERO;
    @Column(nullable=false) private String status = "ACTIVE";
    @Column(nullable=false) private String accountType = "SAVINGS";
    private String mobileNumber;
    private LocalDate dateOfBirth;
    @Column(length=500) private String address;
    private String nomineeName;
    private String nomineeRelationship;
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne(optional=false) private User customer;

    public Long getId(){return id;}
    public String getAccountNumber(){return accountNumber;}
    public void setAccountNumber(String v){accountNumber=v;}
    public BigDecimal getBalance(){return balance;}
    public void setBalance(BigDecimal v){balance=v;}
    public String getStatus(){return status;}
    public void setStatus(String v){status=v;}
    public String getAccountType(){return accountType;}
    public void setAccountType(String v){accountType=v;}
    public String getMobileNumber(){return mobileNumber;}
    public void setMobileNumber(String v){mobileNumber=v;}
    public LocalDate getDateOfBirth(){return dateOfBirth;}
    public void setDateOfBirth(LocalDate v){dateOfBirth=v;}
    public String getAddress(){return address;}
    public void setAddress(String v){address=v;}
    public String getNomineeName(){return nomineeName;}
    public void setNomineeName(String v){nomineeName=v;}
    public String getNomineeRelationship(){return nomineeRelationship;}
    public void setNomineeRelationship(String v){nomineeRelationship=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public User getCustomer(){return customer;}
    public void setCustomer(User v){customer=v;}
}
