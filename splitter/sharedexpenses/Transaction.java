package splitter.sharedexpenses;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long transactionId;

    private final LocalDate date;
    private final String peoplePair;
    private final String reversePair;
    private final String type;
    private final double amount;

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
