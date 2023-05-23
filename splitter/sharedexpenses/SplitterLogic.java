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
    private final HashMap<String, Double> pairBalance;
    private final TransactionRepository transactionRepository;
    private final GroupRepository groupRepository;
    private final PersonRepository personRepository;

    @Autowired
    public SplitterLogic(TransactionRepository transactionRepository, GroupRepository groupRepository, PersonRepository personRepository) {
        this.pairBalance = new HashMap<>();
        this.transactionRepository = transactionRepository;
        this.groupRepository = groupRepository;
        this.personRepository = personRepository;
    }

    public ArrayList<GroupOfPeople> getGroups() {
        return (ArrayList<GroupOfPeople>) groupRepository.findAll();
    }

    public void addTransaction(Transaction t) {
        transactionRepository.save(t);
    }

    public void addTransaction(LocalDate date, String type, String borrower, String lender, double amount) {
        String peoplePair = borrower + " " + lender;
        String reversePair = lender + " " + borrower;

        Transaction transaction = new Transaction(date, type, peoplePair, reversePair, amount);
        updatePairBalance(transaction, this.pairBalance);
        transactionRepository.save(transaction);
    }

    public GroupOfPeople getGroup(String groupName) {
        if (groupRepository.existsByName(groupName)) {
            return groupRepository.findByName(groupName);
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

    public Map<String, Double> getBalance(String input) {
        LocalDate date = dateIncludedOrNot(input);
        HashMap<String, Double> pairBalanceCalculatedWithDate = new HashMap<>();
        String status = input.contains("open") ? "open" : "close";
        List<Transaction> allTransactions = (List<Transaction>) transactionRepository.findAll();

        switch (status) {
            case "open" -> {
                for (Transaction transaction : allTransactions) {
                    if (transaction.getDate().isBefore(date.withDayOfMonth(1))) {
                        updatePairBalance(transaction, pairBalanceCalculatedWithDate);
                    }
                }
            }
            case "close" -> {
                for (Transaction transaction : allTransactions) {
                    if (!transaction.getDate().isAfter(date)) {
                        updatePairBalance(transaction, pairBalanceCalculatedWithDate);
                    }
                }
            }
        }
        return swapAndSortMap(pairBalanceCalculatedWithDate);
    }

    public void printBalance(String input) {
        Map<String, Double> sortedMap = getBalance(input);

        boolean anyRepayments = false;
        String[] names;

        for (String key : sortedMap.keySet()) {
            double value = sortedMap.get(key);
            if (value > 0.01) {
                names = key.split(" ");
                System.out.printf("%s owes %s %.2f\n", names[0], names[1], value);
                anyRepayments = true;
            }
        }

        if (!anyRepayments) {
            System.out.println("No repayments");
        }
    }

    public LocalDate dateIncludedOrNot(String input) {
        Matcher matcher = patternMatcher(RegexPatterns.DATE_PATTERN, input);
        if (matcher.find()) {
            return LocalDate.parse(matcher.group().replace(".", "-"));
        } else {
            return LocalDate.now();
        }
    }

    public Map<String, Double> swapAndSortMap(HashMap<String, Double> balancesMap) {
        Map<String, Double> sortedMap = new TreeMap<>();

        for (Map.Entry<String, Double> entry : balancesMap.entrySet()) {
            String key = entry.getKey();
            double value = entry.getValue();

            if (value < 0) {
                String[] names = key.split(" ");
                if (names.length == 2) {
                    String name1 = names[0];
                    String name2 = names[1];
                    key = name2 + " " + name1;
                }
            }
            value = Math.abs(value);
            sortedMap.put(key, value);
        }
        return sortedMap;
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
        //TODO: Refactor createGroup / addGroup / addToGroup so groupRepository.findByName(name) return unique results
        List<Person> peopleToCreateGroupFrom = extractTemporaryGroupFromInput(listOfPeopleAndGroups).getPeople();
        addPeople(peopleToCreateGroupFrom);

        GroupOfPeople createdGroup = new GroupOfPeople(groupName, peopleToCreateGroupFrom);
        groupRepository.saveOrUpdate(createdGroup);
    }

    public void addGroup(GroupOfPeople group) {
        groupRepository.saveOrUpdate(group);
    }

    public void addPeople(List<Person> peopleToAdd) {
        personRepository.saveAll(peopleToAdd);
    }

    public void addToGroup(String groupNameToAddTo, String peopleToAdd) {
        GroupOfPeople groupToAddTo = getGroup(groupNameToAddTo);
        GroupOfPeople groupToAdd = extractTemporaryGroupFromInput(peopleToAdd);

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
        GroupOfPeople fetchedGroup = groupRepository.findByName(nameOfGroup);

        if (fetchedGroup != null) {
            for (Person person : fetchedGroup.getPeople()) {
                System.out.println(person.getName());
            }
        } else {
            System.out.println("Unknown group");
        }
    }

    public void collectAllPeopleExceptPrefixMinus(GroupOfPeople TMP, String listOfPersonsAndOrGroups) {
        Matcher matcher = patternMatcher(RegexPatterns.EXTRACT_NAME_OR_GROUP, listOfPersonsAndOrGroups);

        while (matcher.find()) {
            if (isStringUpperCase(matcher.group("name"))) { // it's a group
                GroupOfPeople fetchedGroup = groupRepository.findByName(matcher.group("name"));
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
                GroupOfPeople group = groupRepository.findByName(matcher.group("nameToRemove"));
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
        //TODO: refactor, use actual BigDecimal
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
                transactionRepository.deleteByDateIsLessThan(date.withDayOfMonth(1));
            }
            case "close" -> {
                if (date.isEqual(LocalDate.now())) { // date is write off everything before today
                    transactionRepository.deleteByDateIsLessThanEqual(LocalDate.now());
                    break;
                }
                transactionRepository.deleteByDateIsLessThanEqual(date);
            }
        }
    }
}



