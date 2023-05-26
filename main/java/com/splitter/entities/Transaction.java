package com.splitter.entities;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
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

    private String peoplePair;
    private String reversePair;
    private String type;
    private double amount;

    public Transaction() {}

    public Transaction(LocalDate date, String type, String peoplePair, String reversePair, double amount) {
        this.date = date;
        this.type = type;
        this.peoplePair = peoplePair;
        this.reversePair = reversePair;
        this.amount = amount;
    }

    public String getPeoplePair() {
        return peoplePair;
    }

    public String getReversePair() {
        return reversePair;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }
}
