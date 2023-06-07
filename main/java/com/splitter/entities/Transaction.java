package com.splitter.entities;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "TRANSACTIONS")
public class Transaction {

    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @DateTimeFormat
    @Column(columnDefinition = "DATE")
    private LocalDate date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "borrower")
    private Person borrower;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lender")
    private Person lender;

    private String type;
    private BigDecimal amount;

    public Transaction() {}

    public Transaction(LocalDate date, String type, Person borrower, Person lender, BigDecimal amount) {
        this.date = date;
        this.type = type;
        this.borrower = borrower;
        this.lender = lender;
        this.amount = amount;
    }

    public Person getBorrower() {
        return borrower;
    }

    public Person getLender() {
        return lender;
    }

    public String getPeoplePair() {
        return getBorrower().getName() + " " + getLender().getName();
    }

    public String getReversePair() {
        return getLender().getName() + " " + getBorrower().getName();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }
}
