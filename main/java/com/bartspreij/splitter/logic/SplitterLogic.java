package com.bartspreij.splitter.logic;

import com.bartspreij.splitter.cli.GroupOption;
import com.bartspreij.splitter.cli.UsageOption;
import com.bartspreij.splitter.model.GroupOfPeople;
import com.bartspreij.splitter.model.Person;
import com.bartspreij.splitter.model.Transaction;
import com.bartspreij.splitter.repository.GroupRepository;
import com.bartspreij.splitter.repository.PersonRepository;
import com.bartspreij.splitter.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitterLogic {
    private final TransactionRepository transactionRepository;
    private final GroupRepository groupRepository;
    private final PersonRepository personRepository;

    @Autowired
    public SplitterLogic(TransactionRepository transactionRepository, GroupRepository groupRepository, PersonRepository personRepository) {
        this.transactionRepository = transactionRepository;
        this.groupRepository = groupRepository;
        this.personRepository = personRepository;
    }

    public void addTransaction(LocalDate date, String type, Person borrower, Person lender, double amount) {
        String peoplePair = borrower.getName() + " " + lender.getName();
        String reversePair = lender.getName() + " " + borrower.getName();

        Transaction transaction = new Transaction(date, type, peoplePair, reversePair, amount);
        transactionRepository.save(transaction);
    }

    public GroupOfPeople getGroup(String groupName) {
        Optional<GroupOfPeople> fetchedGroup = groupRepository.findByName(groupName);

        return fetchedGroup.orElseGet(GroupOfPeople::new);
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
            Person borrower = fetchOrAddPerson(matcher.group("borrower"));
            Person lender = fetchOrAddPerson(matcher.group("lender"));
            double amount = Double.parseDouble(matcher.group("amount"));
            addTransaction(date, type, borrower, lender, amount);
        } else {
            System.out.println("Illegal command arguments");
        }
    }

    public Person fetchOrAddPerson(String name) {
        Person person = personRepository.findByName(name);

        if (person == null) {
            person = new Person(name);
            personRepository.save(person);
        }
        return person;
    }

    public List<Person> fetchOrAddParticipants(String participants) {
        List<Person> participantsGroup = extractTemporaryGroupFromInput(participants);
        List<Person> participantsList = new ArrayList<>();
        for (Person p : participantsGroup) { // check db for person if not there add
            Person person = fetchOrAddPerson(p.getName());
            participantsList.add(person);
        }
        return participantsList;
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

        if (!groupRepository.existsByName(groupName)) {
            List<Person> groupList = fetchOrAddParticipants(listOfPeopleAndGroups);
            GroupOfPeople newGroup = new GroupOfPeople();
            newGroup.setName(groupName);
            newGroup.setPeople(groupList);

            for (Person p : groupList) {
                if (!personRepository.existsByName(p.getName())) {
                    personRepository.save(p);
                }
            }
            groupRepository.save(newGroup);
        } else {
            addToGroup(groupName, listOfPeopleAndGroups);
        }
    }

    public void addToGroup(String groupNameToAddTo, String peopleToAdd) {
        List<Person> groupToAdd = fetchOrAddParticipants(peopleToAdd);
        GroupOfPeople groupToAddTo = getGroup(groupNameToAddTo);

        for (Person person : groupToAdd) {
            if (!personRepository.existsByName(person.getName())) {
                groupToAddTo.add(person);
            }
        }
        groupRepository.save(groupToAddTo);
    }

    public void removeGroup(String groupName, String listOfPeopleAndGroups) {
        GroupOfPeople groupToRemoveFrom = getGroup(groupName);
        List<Person> groupToRemove = extractTemporaryGroupFromInput(listOfPeopleAndGroups);
        
        for (Person person : groupToRemove) {
            groupToRemoveFrom.remove(person);
        }
    }

    public void showGroup(String nameOfGroup) {
        GroupOfPeople fetchedGroup = getGroup(nameOfGroup);

        if (fetchedGroup != null) {
            fetchedGroup.printGroup();
        } else {
            System.out.println("Unknown group");
        }
    }

    public void collectAllPeopleExceptPrefixMinus(List<Person> TMP, String listOfPersonsAndOrGroups) {
        Matcher matcher = patternMatcher(RegexPatterns.EXTRACT_NAME_OR_GROUP, listOfPersonsAndOrGroups);

        while (matcher.find()) {
            String name = matcher.group("name");
            if (isStringUpperCase(name)) { // it's a group
                GroupOfPeople fetchedGroup = getGroup(name);
                TMP.addAll(fetchedGroup.getPeople());
            } else { // it's a person
                TMP.add(new Person(name));
            }
        }
    }

    public void removePrefixMinusFromGroup(List<Person> TMP, String toRemove) {
        Matcher matcher = patternMatcher(RegexPatterns.NAMES_TO_REMOVE, toRemove);

        while (matcher.find()) {
            TMP.removeIf(person -> person.getName().equals(matcher.group("nameToRemove")));
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

    public List<Person> extractTemporaryGroupFromInput(String listOfPeopleAndGroups) {
        List<Person> TMP = new ArrayList<>();
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
            Person payer = fetchOrAddPerson(matcher.group(1));
            String product = matcher.group(2); // not used atm
            double amount = Double.parseDouble(matcher.group(3));
            List<Person> participants = fetchOrAddParticipants(matcher.group(4));
            double adjustedAmount = isCashBack ? -amount : amount;
            purchase(date, payer, product, adjustedAmount, participants);
        }
    }

    @Transactional
    public void purchaseExtractInfo(String input) {
        extractInfo(input, false);
    }

    @Transactional
    public void cashBackExtractInfo(String input) {
        extractInfo(input, true);
    }

    @Transactional
    public void purchase(LocalDate date, Person payer, String product, double amount, List<Person> participantsList) {
        //TODO: refactor, use actual BigDecimal
        if (participantsList.size() == 0) {
            System.out.println("Group is empty");
            return;
        }

        // Constants
        final double CENT = 0.01;
        final int DECIMAL_PLACES = 2;

        // Turn values into BigDecimal
        BigDecimal amountDB = BigDecimal.valueOf(amount);
        BigDecimal sizeOfGroup = BigDecimal.valueOf(participantsList.size());

        // Calculate amount per person and remainder
        BigDecimal amountPerPerson = amountDB.divide(sizeOfGroup, DECIMAL_PLACES, RoundingMode.DOWN);
        BigDecimal remainderBD = amountDB.subtract(amountPerPerson.multiply(sizeOfGroup));

        // back to double
        amount = amountPerPerson.doubleValue();

        for (Person person : participantsList) {
            if (person.equals(payer)) {
                remainderBD = remainderBD.subtract(BigDecimal.valueOf(CENT)); // remainder left or not remove cent
                continue;
            }
            if (remainderBD.compareTo(BigDecimal.ZERO) > 0) { // while there is remainder left
                addTransaction(date, "borrow", person, payer, amount + CENT);
                remainderBD = remainderBD.subtract(BigDecimal.valueOf(CENT)); // we remove the cent
            } else {
                addTransaction(date, "borrow", person, payer, amount);
            }
        }
    }

    public Matcher patternMatcher(String REGEX, String input) {
        Pattern pattern = Pattern.compile(REGEX);
        return pattern.matcher(input);
    }

    @Transactional
    public void secretSanta(String input) {
        String groupName = input.split(" ")[1];
        List<Person> peopleRandomized = getGroup(groupName).getPeople();
        List<Person> peopleSorted = new ArrayList<>(peopleRandomized);
        Collections.sort(peopleSorted);
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
            case "open" -> transactionRepository.deleteByDateIsLessThan(date.withDayOfMonth(1));
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



