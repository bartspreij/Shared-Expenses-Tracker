package splitter.sharedexpenses;

import java.time.LocalDate;
import java.util.Date;

public class Transaction {
    private LocalDate date;
    private String type;
    private String peoplePair;
    private String reversePair;
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
