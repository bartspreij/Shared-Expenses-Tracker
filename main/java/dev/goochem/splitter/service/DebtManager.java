package dev.goochem.splitter.service;

import java.util.HashMap;
import java.util.Map;

public class DebtManager {
    private Map<String, Map<String, Double>> debts;

    public DebtManager() {
        debts = new HashMap<>();
    }

    public void borrow(String from, String to, double amount) {
        if (!debts.containsKey(from)) {
            debts.put(from, new HashMap<>());
        }

        Map<String, Double> fromDebts = debts.get(from);
        fromDebts.put(to, fromDebts.getOrDefault(to, 0.0) + amount);
    }

    public void balanceClose() {
        for (String from : debts.keySet()) {
            Map<String, Double> fromDebts = debts.get(from);
            for (String to : fromDebts.keySet()) {
                double amount = fromDebts.get(to);
                System.out.printf("%s owes %s %.2f\n", from, to, amount);
            }
        }
    }

    public void balancePerfect() {
        boolean updated;
        do {
            updated = false;

            for (String from : debts.keySet()) {
                Map<String, Double> fromDebts = debts.get(from);
                for (String to : fromDebts.keySet()) {
                    double amount = fromDebts.get(to);

                    if (debts.containsKey(to) && debts.get(to).containsKey(from)) {
                        double reverseAmount = debts.get(to).get(from);

                        if (amount > reverseAmount) {
                            debts.get(from).put(to, amount - reverseAmount);
                            debts.get(to).remove(from);
                            updated = true;
                        } else if (amount < reverseAmount) {
                            debts.get(to).put(from, reverseAmount - amount);
                            debts.get(from).remove(to);
                            updated = true;
                        } else {
                            debts.get(from).remove(to);
                            debts.get(to).remove(from);
                            updated = true;
                        }
                    }
                }
            }
        } while (updated);

        // Output the updated debts after reducing the amounts owed
        for (String from : debts.keySet()) {
            Map<String, Double> fromDebts = debts.get(from);
            for (String to : fromDebts.keySet()) {
                double amount = fromDebts.get(to);
                System.out.printf("%s owes %s %.2f\n", from, to, amount);
            }
        }
    }
}
