package com.splitter.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@Data
@Entity
@Table(name = "TRANSACTIONS")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long transactionId;

    @Column(columnDefinition = "DATE")
    private LocalDate date;

    private String peoplePair;
    private String reversePair;
    private String type;
    private double amount;

    public Transaction(LocalDate date, String type, String peoplePair, String reversePair, double amount) {
        this.date = date;
        this.type = type;
        this.peoplePair = peoplePair;
        this.reversePair = reversePair;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getType() {
        return type;
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

    @Override
    public String toString() {
        return date.toString() + " " + type + " " + peoplePair + " " + amount;
    }
}
