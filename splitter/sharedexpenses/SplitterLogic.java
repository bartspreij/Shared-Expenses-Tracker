package splitter.sharedexpenses;

import org.springframework.beans.factory.annotation.Autowired;
import splitter.database.GroupRepository;
import splitter.database.PersonRepository;
import splitter.database.TransactionRepository;
import splitter.userinterface.GroupOption;
import splitter.userinterface.UsageOption;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitterLogic {
    private final ArrayList<Transaction> transactions;
    private final ArrayList<GroupOfPeople> groups;
    private final HashMap<String, Double> pairBalance;
    private final TransactionRepository transactionRepository;
    private final GroupRepository groupRepository;
    private final PersonRepository personRepository;

    @Autowired
    public SplitterLogic(TransactionRepository transactionRepository, GroupRepository groupRepository, PersonRepository personRepository) {
        this.transactions = new ArrayList<>();
        this.pairBalance = new HashMap<>();
        this.groups = new ArrayList<>();
        this.transactionRepository = transactionRepository;
        this.groupRepository = groupRepository;
        this.personRepository = personRepository;
    }

    public ArrayList<GroupOfPeople> getGroups() {
        return groups;
    }

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public void addTransaction(LocalDate date, String type, String borrower, String lender, double amount) {
        String peoplePair = borrower + " " + lender;
        String reversePair = lender + " " + borrower;

        Transaction transaction = new Transaction(date, type, peoplePair, reversePair, amount);
        updatePairBalance(transaction, this.pairBalance);
        transactions.add(transaction);
        transactionRepository.save(transaction);
    }

    public GroupOfPeople getGroup(String grpName) {
        for (GroupOfPeople group : groups) {
            if (group.getName().equals(grpName)) {
                return group;
            }
        }
        return null;
    }

    public UsageOption getUsageOption(String input) {
        String[] usageString = input.split(" ");

        for (UsageOption option : UsageOption.values()) {
            for (String word : usageString) {
                if (option.toString().equals(word)) {
                    return option;
                }
            }
        }
        return UsageOption.DEFAULT;
    }

    public GroupOption getGroupOption(String input) {
        String[] usageString = input.toUpperCase().split(" ");

        for (GroupOption option : GroupOption.values()) {
            for (String word : usageString) {
                if (option.name().equals(word)) {
                    return option;
                }
            }
        }
        return GroupOption.INVALID;
    }

    public void updatePairBalance(Transaction t, HashMap<String, Double> pairBalancesToCalculate) {
        String key = "No key";

        if (pairBalancesToCalculate.containsKey(t.getPeoplePair())) {
            key = "keyIsPeoplePair";
        } else if (pairBalancesToCalculate.containsKey(t.getReversePair())) {
            key = "keyIsReversePair";
        }

        switch (key) {
            case "keyIsPeoplePair" -> {
                if (t.getType().equals("borrow")) {
                    pairBalancesToCalculate.merge(t.getPeoplePair(), t.getAmount(), Double::sum);
                } else if (t.getType().equals("repay")) {
                    pairBalancesToCalculate.merge(t.getPeoplePair(), -t.getAmount(), Double::sum);
                }
            }
            case "keyIsReversePair" -> {
                if (t.getType().equals("borrow")) {
                    pairBalancesToCalculate.merge(t.getReversePair(), -t.getAmount(), Double::sum);
                } else if (t.getType().equals("repay")) {
                    pairBalancesToCalculate.merge(t.getReversePair(), t.getAmount(), Double::sum);
                }
            }

            case "No key" -> {
                if (t.getType().equals("borrow")) {
                    pairBalancesToCalculate.putIfAbsent(t.getPeoplePair(), t.getAmount());
                } else if (t.getType().equals("repay")) {
                    pairBalancesToCalculate.putIfAbsent(t.getPeoplePair(), -t.getAmount());
                }
            }
        }
    }

    public void borrowOrRepay(String input) {
        LocalDate date = dateIncludedOrNot(input);
        Matcher matcher = patternMatcher(RegexPatterns.BORROW_OR_REPAY, input);

        if (matcher.find()) {
            String type = matcher.group("type");
            String borrower = matcher.group("borrower");
            String lender = matcher.group("lender");
            double amount = Double.parseDouble(matcher.group("amount"));
            addTransaction(date, type, borrower, lender, amount);
        } else {
            System.out.println("Illegal command arguments");
        }
    }

    public void getBalance(String input) {
        LocalDate date = dateIncludedOrNot(input);
        HashMap<String, Double> pairBalanceCalculatedWithDate = new HashMap<>();
        String openOrClose = input.contains("open") ? "open" : "close";

        switch (openOrClose) {
            case "open" -> {
                for (Transaction transaction : transactions) {
                    if (transaction.getDate().isBefore(date.withDayOfMonth(1))) {
                        updatePairBalance(transaction, pairBalanceCalculatedWithDate);
                    }
                }
            }
            case "close" -> {
                for (Transaction transaction : transactions) {
                    if (!transaction.getDate().isAfter(date)) {
                        updatePairBalance(transaction, pairBalanceCalculatedWithDate);
                    }
                }
            }
        }
        printBalance(pairBalanceCalculatedWithDate); // print balance
    }

    public LocalDate dateIncludedOrNot(String input) {
        Matcher matcher = patternMatcher(RegexPatterns.DATE_PATTERN, input);
        if (matcher.find()) {
            return LocalDate.parse(matcher.group().replace(".", "-"));
        } else {
            return LocalDate.now();
        }
    }

    public void printBalance(HashMap<String, Double> mapToPrint) {
        TreeMap<String, Double> sortedMap = new TreeMap<>(mapToPrint);


        boolean anyRepayments = false;
        String[] names;

        for (String key : sortedMap.keySet()) {
            if (mapToPrint.get(key) > 0.01) {
                names = key.split(" ");
                System.out.printf("%s owes %s %.2f\n", names[0], names[1], sortedMap.get(key));
                anyRepayments = true;

            } else if (sortedMap.get(key) < -0.01) {
                names = key.split(" ");
                System.out.printf("%s owes %s %.2f\n", names[1], names[0], Math.abs(sortedMap.get(key)));
                anyRepayments = true;
            }
        }
        if (!anyRepayments) {
            System.out.println("No repayments need");
        }
    }

    public void validateGroupInput(String input) {
        GroupOption option = getGroupOption(input);

        String regex = RegexPatterns.getGroupRegex(option.name());
        Matcher matcher = patternMatcher(regex, input);

        while (matcher.find()) {
            String groupName = matcher.group("groupName");
            String personsAndOrGroups = matcher.group("personsAndOrGroups");

            switch (option) {
                case ADD -> addToGroup(groupName, personsAndOrGroups);
                case SHOW -> showGroup(matcher.group(1));
                case CREATE -> {
                    if (!isStringUpperCase(groupName)) { // invalid group name
                        System.out.println("Illegal command arguments");
                        break;
                    }
                    createGroup(groupName, personsAndOrGroups);
                }
                case REMOVE -> removeGroup(groupName, personsAndOrGroups);
                default -> System.out.println("Illegal command arguments");
            }
        }
    }

    public void createGroup(String groupName, String listOfPeopleAndGroups) {
        List<Person> peopleToCreateGroupFrom = extractTemporaryGroupFromInput(listOfPeopleAndGroups).getPeople();
        personRepository.saveAll(peopleToCreateGroupFrom);
        groupRepository.save(new GroupOfPeople(groupName, peopleToCreateGroupFrom));
        groups.add(new GroupOfPeople(groupName, peopleToCreateGroupFrom));

    }

    public void addGroup(GroupOfPeople group) {
        groups.add(group);
    }

    public void addPerson() {

    }

    public void addToGroup(String groupName, String listOfPeopleAndGroups) {
        GroupOfPeople groupToAddTo = getGroup(groupName);
        GroupOfPeople groupToAdd = extractTemporaryGroupFromInput(listOfPeopleAndGroups);

        for (Person person : groupToAdd.getPeople()) {
            groupToAddTo.add(person);
        }
    }

    public void removeGroup(String groupName, String listOfPeopleAndGroups) {
        GroupOfPeople groupToRemoveFrom = getGroup(groupName);
        GroupOfPeople groupToRemove = extractTemporaryGroupFromInput(listOfPeopleAndGroups);
        
        for (Person person : groupToRemove.getPeople()) {
            groupToRemoveFrom.remove(person);
        }
    }

    public void showGroup(String nameOfGroup) {
        for (GroupOfPeople group : groups) {
            if (group.getName().equals(nameOfGroup)) {
                group.printGroup();
                return;
            }
        }
        System.out.println("Unknown group");
    }

    public void collectAllPeopleExceptPrefixMinus(GroupOfPeople TMP, String listOfPersonsAndOrGroups) {
        Matcher matcher = patternMatcher(RegexPatterns.EXTRACT_NAME_OR_GROUP, listOfPersonsAndOrGroups);

        while (matcher.find()) {
            if (isStringUpperCase(matcher.group("name"))) { // it's a group
                GroupOfPeople fetchedGroup = getGroup(matcher.group("name"));
                for (Person person : fetchedGroup.getPeople()) {
                    TMP.add(person);
                }
            } else { // it's a person
                TMP.add(new Person(matcher.group("name")));
            }
        }
    }

    public void removePrefixMinusFromGroup(GroupOfPeople TMP, String toRemove) {
        Matcher matcher = patternMatcher(RegexPatterns.NAMES_TO_REMOVE, toRemove);

        while (matcher.find()) {
            TMP.getPeople().removeIf(person -> person.getName().equals(matcher.group("nameToRemove")));
        }
        matcher.reset();
        while (matcher.find()) {
            if (isStringUpperCase(matcher.group("nameToRemove"))) { // it's a group
                GroupOfPeople group = getGroup(matcher.group("nameToRemove"));
                for (Person person : group.getPeople()) {
                    TMP.remove(person);
                }
            }
        }
    }

    public GroupOfPeople extractTemporaryGroupFromInput(String listOfPeopleAndGroups) {
        GroupOfPeople TMP = new GroupOfPeople("TMP", new ArrayList<>());
        collectAllPeopleExceptPrefixMinus(TMP, listOfPeopleAndGroups);
        removePrefixMinusFromGroup(TMP, listOfPeopleAndGroups);

        return TMP;
    }

    public boolean isStringUpperCase(String str) {
        char[] charArray = str.toCharArray();

        for (char c : charArray) { // if any character is not in upper case, return false
            if (!Character.isUpperCase(c)) {
                return false;
            }
        }
        return true;
    }

    public void extractInfo(String input, boolean isCashBack) {
        LocalDate date = dateIncludedOrNot(input);
        Matcher matcher = patternMatcher(isCashBack ? RegexPatterns.CASHBACK_EXTRACT_INFO : RegexPatterns.PURCHASE_EXTRACT_INFO, input);

        if (matcher.find()) {
            String payer = matcher.group(1);
            String product = matcher.group(2); // not used atm
            double amount = Double.parseDouble(matcher.group(3));
            String participants = matcher.group(4);
            double adjustedAmount = isCashBack ? -amount : amount;
            purchase(date, payer, product, adjustedAmount, participants);
        }
    }

    public void purchaseExtractInfo(String input) {
        extractInfo(input, false);
    }

    public void cashBackExtractInfo(String input) {
        extractInfo(input, true);
    }

    public void purchase(LocalDate date, String payer, String product, double amount, String participants) {
        // add all participants to participantsGroup group
        GroupOfPeople participantsGroup = extractTemporaryGroupFromInput(participants);

        if (participantsGroup.getSizeOfGroup() == 0) {
            System.out.println("Group is empty");
            return;
        }

        // Constants
        final double CENT = 0.01;
        final int DECIMAL_PLACES = 2;

        // Turn values into BigDecimal
        BigDecimal amountDB = BigDecimal.valueOf(amount);
        BigDecimal sizeOfGroup = BigDecimal.valueOf(participantsGroup.getSizeOfGroup());

        // Calculate amount per person and remainder
        BigDecimal amountPerPerson = amountDB.divide(sizeOfGroup, DECIMAL_PLACES, RoundingMode.DOWN);
        BigDecimal remainderBD = amountDB.subtract(amountPerPerson.multiply(sizeOfGroup));

        // back to double
        amount = amountPerPerson.doubleValue();

        for (Person person : participantsGroup.getPeople()) {
            if (person.getName().equals(payer)) {
                remainderBD = remainderBD.subtract(BigDecimal.valueOf(CENT)); // remainder left or not remove cent
                continue;
            }
            if (remainderBD.compareTo(BigDecimal.ZERO) > 0) { // while there is remainder left
                addTransaction(date, "borrow", person.getName(), payer, amount + CENT);
                remainderBD = remainderBD.subtract(BigDecimal.valueOf(CENT)); // we remove the cent
            } else {
                addTransaction(date, "borrow", person.getName(), payer, amount);
            }
        }
    }

    public Matcher patternMatcher(String REGEX, String input) {
        Pattern pattern = Pattern.compile(REGEX);
        return pattern.matcher(input);
    }

    public void secretSanta(String input) {
        String groupName = input.split(" ")[1];

        List<Person> peopleSorted = getGroup(groupName).getPeople();
        List<Person> peopleRandomized = new ArrayList<>(peopleSorted);
        Random random = new Random();
        Collections.shuffle(peopleRandomized, random);

        for (Person giver : peopleSorted) {
            for (Person receiver  : peopleRandomized) {

                if (!giver.equals(receiver) &&
                        (receiver.getSecretSantaRecipient() == null || !receiver.getSecretSantaRecipient().equals(giver))) {

                    System.out.printf("%s gift to %s\n", giver, receiver);
                    giver.setSecretSantaRecipient(receiver);
                    peopleRandomized.remove(receiver);
                    break;
                }
            }
        }
    }

    @Transactional
    public void writeOff(String input) {
        LocalDate date = dateIncludedOrNot(input);
        String openOrClose = input.contains("open") ? "open" : "close";

        switch (openOrClose) {
            case "open" -> {
                transactions.removeIf(transaction -> transaction.getDate().isBefore(date.withDayOfMonth(1)));
                transactionRepository.deleteByDateIsLessThan(date.withDayOfMonth(1));
            }
            case "close" -> {
                if (date.isEqual(LocalDate.now())) { // date is write off everything before today
                    transactions.removeIf(transaction -> !transaction.getDate().isAfter(LocalDate.now()));
                    transactionRepository.deleteByDateIsLessThanEqual(LocalDate.now());
                    break;
                }
                transactions.removeIf(transaction -> !transaction.getDate().isAfter(date));
                transactionRepository.deleteByDateIsLessThanEqual(date);
            }
        }
    }
}



