package dev.goochem.splitter.service;

import dev.goochem.splitter.cli.GroupOption;
import dev.goochem.splitter.cli.RegexPatterns;
import dev.goochem.splitter.cli.UsageOption;
import dev.goochem.splitter.entities.GroupOfPeople;
import dev.goochem.splitter.entities.Person;
import dev.goochem.splitter.entities.Transaction;
import dev.goochem.splitter.graph.ExpenseGraph;
import dev.goochem.splitter.graph.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SplitterService {
    private final TransactionService transactionService;
    private final GroupService groupService;
    private final PersonService personService;

    @Autowired
    public SplitterService(TransactionService transactionService,
                         GroupService groupService,
                         PersonService personService) {
        this.transactionService = transactionService;
        this.groupService = groupService;
        this.personService = personService;
    }

    public void addTransaction(LocalDate date, String type, Person borrower, Person lender, BigDecimal amount) {
        transactionService.addTransaction(new Transaction(date, type, borrower, lender, amount));
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

    public void updatePairBalance(Transaction t, Map<String, BigDecimal> pairBalancesToCalculate) {
        String key = "No key";

        if (pairBalancesToCalculate.containsKey(t.getPeoplePair())) {
            key = "keyIsPeoplePair";
        } else if (pairBalancesToCalculate.containsKey(t.getReversePair())) {
            key = "keyIsReversePair";
        }

        switch (key) {
            case "keyIsPeoplePair" -> {
                if (t.getType().equals("borrow")) {
                    pairBalancesToCalculate.merge(t.getPeoplePair(), t.getAmount(), BigDecimal::add);
                } else if (t.getType().equals("repay")) {
                    pairBalancesToCalculate.merge(t.getPeoplePair(), t.getAmount().negate(), BigDecimal::add);
                }
            }
            case "keyIsReversePair" -> {
                if (t.getType().equals("borrow")) {
                    pairBalancesToCalculate.merge(t.getReversePair(), t.getAmount().negate(), BigDecimal::add);
                } else if (t.getType().equals("repay")) {
                    pairBalancesToCalculate.merge(t.getReversePair(), t.getAmount(), BigDecimal::add);
                }
            }
            case "No key" -> { // new pair
                if (t.getType().equals("borrow")) {
                    pairBalancesToCalculate.putIfAbsent(t.getPeoplePair(), t.getAmount());
                } else if (t.getType().equals("repay")) {
                    pairBalancesToCalculate.putIfAbsent(t.getPeoplePair(), t.getAmount().negate());
                }
            }
        }
    }

    @Transactional
    public void borrow(String input) {
        borrowOrRepay(input, false);
    }

    @Transactional
    public void repay(String input) {
        borrowOrRepay(input, true);
    }

    public void borrowOrRepay(String input, boolean isRepay) {
        LocalDate date = dateIncludedOrNot(input);
        Matcher matcher = patternMatcher(RegexPatterns.BORROW_OR_REPAY, input);

        if (matcher.find()) {
            String type = matcher.group("type");
            Person borrower = personService.getOrAdd(matcher.group("borrower"));
            Person lender = personService.getOrAdd(matcher.group("lender"));
            BigDecimal amount = new BigDecimal(matcher.group("amount"));
            addTransaction(date, type, borrower, lender, amount);

        } else {
            System.out.println("Illegal command arguments");
        }
    }

    public boolean isThereASubGroup(String input) {
        Matcher matcher = patternMatcher(RegexPatterns.PERSONS_OR_GROUPS, input);
        return matcher.find();
    }

    public List<String> getSubGroup(String input) {
        Matcher matcher = patternMatcher(RegexPatterns.PERSONS_OR_GROUPS, input);
        String personsAndOrGroups = "";
        while (matcher.find()) {
            personsAndOrGroups = matcher.group("personsAndOrGroups");
        }
        return fetchParticipants(personsAndOrGroups).stream()
                .map(Person::getName)
                .toList();
    }

    public Map<String, BigDecimal> getBalance(String input) {
        String status = input.contains("open") ? "open" : "close"; // default is close
        LocalDate date = status.equals("close") ? dateIncludedOrNot(input) : dateIncludedOrNot(input).withDayOfMonth(1).minusDays(1);

        Map<String, BigDecimal> pairBalanceMap = new HashMap<>();
        List<Transaction> transactions = transactionService.getTransactionsBeforeAGivenDate(date);

        for (Transaction t : transactions) {
            updatePairBalance(t, pairBalanceMap);
        }
        return swapAndSortMap(pairBalanceMap);
    }

    public void printBalance(String input) {
        // TODO: fix this garbage
        List<String> subGroup = new ArrayList<>();
        if (isThereASubGroup(input)) {
            subGroup = getSubGroup(input);
            if (subGroup.isEmpty()) {
                System.out.println("Group is empty");
                return;
            } else if (subGroup.get(0).equals("Unknown group")) {
                System.out.println("Group does not exist");
                return;
            }
        }

        Map<String, BigDecimal> sortedMap = getBalance(input);
        boolean anyRepayments = false;
        String[] names;
        for (String key : sortedMap.keySet()) {
            BigDecimal value = sortedMap.get(key);
            if (value.compareTo(BigDecimal.ZERO) > 0) {
                names = key.split(" ");
                if (subGroup.isEmpty() || subGroup.contains(names[0])) {
                    System.out.printf("%s owes %s %.2f\n", names[0], names[1], value);
                    anyRepayments = true;
                }
            }
        }
        if (!anyRepayments) {
            System.out.println("No repayments");
        }
    }

    public void printBalancePerfect(String input) {
        ExpenseGraph graph = new ExpenseGraph();
        Map<String, BigDecimal> sortedMap = getBalance(input);

        // Strip nodes
        Set<String> namesForNodes = new HashSet<>(); // no duplicate root nodes
        for (String key : sortedMap.keySet()) {
            String name = key.split(" ")[0];
            namesForNodes.add(name);
        }

        // Add new nodes
        for (String name : namesForNodes) {
            graph.addNode(new Node(name));
        }

        // Add edges
        for (Map.Entry<String, BigDecimal> entry : sortedMap.entrySet()) {
            String key = entry.getKey();
            double value = entry.getValue().doubleValue();
            String[] splitNames = key.split(" ");
            String name1 = splitNames[0];
            String name2 = splitNames[1];

            graph.addEdge(name1, name2, value);
        }

        // Optimize through Folker-Fulkerson algorithm


        graph.print();
    }

    public Map<String, BigDecimal> swapAndSortMap(Map<String, BigDecimal> balancesMap) {
        Map<String, BigDecimal> sortedMap = new TreeMap<>();

        for (Map.Entry<String, BigDecimal> entry : balancesMap.entrySet()) {
            String key = entry.getKey();
            BigDecimal value = entry.getValue();

            if (value.compareTo(BigDecimal.ZERO) < 0) { // swap order if balance is negative, so we always print positive
                String[] splitNames = key.split(" ");
                if (splitNames.length == 2) {
                    String name1 = splitNames[0];
                    String name2 = splitNames[1];
                    key = name2 + " " + name1;
                }
            }
            sortedMap.put(key, value.abs());
        }
        return sortedMap;
    }

    public LocalDate dateIncludedOrNot(String input) {
        Matcher matcher = patternMatcher(RegexPatterns.DATE_PATTERN, input);
        if (matcher.find()) {
            return LocalDate.parse(matcher.group().replace(".", "-"));
        } else {
            return LocalDate.now();
        }
    }

    public void validateGroupInput(String input) {
        GroupOption option = getGroupOption(input);

        String regex = RegexPatterns.getGroupRegex(option.name());
        Matcher matcher = patternMatcher(regex, input);

        while (matcher.find()) {
            String groupName = matcher.group("groupName");
            Set<Person> listOfPeople = new HashSet<>();
            if (!option.equals(GroupOption.SHOW)) {
                if (matcher.group("personsAndOrGroups") == null) {
                    System.out.println("Illegal command arguments");
                    break;
                }
                 listOfPeople = fetchParticipants(matcher.group("personsAndOrGroups"));
            }

            switch (option) {
                case ADD -> addToGroup(groupName, listOfPeople);
                case SHOW -> showGroup(matcher.group(1));
                case CREATE -> {
                    if (!isStringUpperCase(groupName)) { // invalid group name
                        System.out.println("Illegal command arguments");
                        break;
                    }
                    groupService.createNewGroup(groupName, listOfPeople);
                }
                case REMOVE -> removeGroup(groupName, listOfPeople);
                default -> System.out.println("Illegal command arguments");
            }
        }
    }

    public void addToGroup(String groupName, Set<Person> listOfPeople) {
        groupService.addMembers(groupName, listOfPeople);
    }

    public void removeGroup(String groupName, Set<Person> listOfPeople) {
        groupService.removeMembers(groupName, listOfPeople);
    }

    public void showGroup(String nameOfGroup) {
        if (!groupService.exists(nameOfGroup)) {
            System.out.println("Unknown group");
        } else if (groupService.getMembers(nameOfGroup).isEmpty()) {
            System.out.println("Group is empty");
        } else {
            System.out.println(groupService.showMembers(nameOfGroup));
        }
    }

    public void collectAllPeopleExceptPrefixMinus(Set<Person> TMP, String listOfPersonsAndOrGroups) {
        Matcher matcher = patternMatcher(RegexPatterns.EXTRACT_NAME_OR_GROUP, listOfPersonsAndOrGroups);

        while (matcher.find()) {
            String name = matcher.group("name");
            if (isStringUpperCase(name)) { // it's a group
                if(!groupService.exists(name)) { // add unknown group to check for if printing
                    TMP.add(new Person("Unknown group"));
                    return;
                }
                for (Person p : groupService.getGroup(name).getPeople()) {
                    TMP.add(personService.getOrAdd(p.getName()));
                }
            } else { // it's a person
                TMP.add(personService.getOrAdd(name));
            }
        }
    }

    public void removePrefixMinusFromGroup(Set<Person> TMP, String toRemove) {
        Matcher matcher = patternMatcher(RegexPatterns.NAMES_TO_REMOVE, toRemove);

        while (matcher.find()) {
            TMP.removeIf(person -> person.getName().equals(matcher.group("nameToRemove")));
        }
        matcher.reset();
        while (matcher.find()) {
            if (isStringUpperCase(matcher.group("nameToRemove"))) { // it's a group
                GroupOfPeople group = groupService.getGroup(matcher.group("nameToRemove"));
                for (Person person : group.getPeople()) {
                    TMP.removeIf(p -> p.getName().equals(person.getName()));
                }
            }
        }
    }

    @Transactional
    public Set<Person> fetchParticipants(String listOfPeopleAndGroups) {
        Set<Person> TMP = new HashSet<>();
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

    public void extractInfo(String input, boolean isCashBack) { // cashBack is a reverse purchase so if cashBack reverse
        LocalDate date = dateIncludedOrNot(input);
        Matcher matcher = patternMatcher(isCashBack ? RegexPatterns.CASHBACK_EXTRACT_INFO : RegexPatterns.PURCHASE_EXTRACT_INFO, input);

        if (matcher.find()) {
            Person payer = personService.getOrAdd(matcher.group(1));
            String product = matcher.group(2); // not used atm
            double amount = Double.parseDouble(matcher.group(3));
            Set<Person> participants = fetchParticipants(matcher.group(4));
            BigDecimal adjustedAmount = BigDecimal.valueOf(isCashBack ? -amount : amount);
            purchase(date, payer, adjustedAmount, participants, isCashBack);
        }
    }

    @Transactional
    public void purchaseExtractInfo(String input) { extractInfo(input, false); }

    @Transactional
    public void cashBackExtractInfo(String input) { extractInfo(input, true); }

    @Transactional
    public void purchase(LocalDate date, Person payer, BigDecimal amount, Set<Person> participantsList, boolean isCashBack) {
        if (participantsList.size() == 0) {
            System.out.println("Group is empty");
            return;
        }

        // Constants
        BigDecimal CENT = BigDecimal.valueOf(0.01);
        BigDecimal sizeOfGroup = BigDecimal.valueOf(participantsList.size());
        final int DECIMAL_PLACES = 2;

        // Calculate amount per person and remainder
        BigDecimal amountPerPerson = amount.divide(sizeOfGroup, DECIMAL_PLACES, RoundingMode.DOWN);
        BigDecimal remainderBD = amount.subtract(amountPerPerson.multiply(sizeOfGroup));

        List<Person> orderedParticipantsList = new ArrayList<>(participantsList);
        Collections.sort(orderedParticipantsList);
        for (Person person : orderedParticipantsList) {
            if (person.equals(payer)) {
                if (remainderBD.compareTo(BigDecimal.ZERO) != 0) {
                    remainderBD = remainderBD.subtract(isCashBack ? CENT.negate() : CENT);
                }
                continue;
            }
            if (remainderBD.compareTo(BigDecimal.ZERO) != 0) { // while there is remainder left
                addTransaction(date, "borrow", person, payer, amountPerPerson.add(isCashBack ? CENT.negate() : CENT));
                remainderBD = isCashBack ? remainderBD.add(CENT) : remainderBD.subtract(CENT); // we remove the cent
            } else {
                addTransaction(date, "borrow", person, payer, amountPerPerson);
            }
        }
    }

    public Matcher patternMatcher(String REGEX, String input) {
        Pattern pattern = Pattern.compile(REGEX);
        return pattern.matcher(input);
    }


    public void secretSanta(String groupName) {
        if (groupService.getGroup(groupName) == null) {
            System.out.println("Unknown group");
        } else {
            List<String> sortedPeople = groupService.getMembers(groupName);
            List<String> randomPeople = new ArrayList<>(sortedPeople);
            Collections.shuffle(randomPeople, new Random());

            for (String giver : sortedPeople) {
                for (String receiver  : randomPeople) {

                    if (!giver.equals(receiver) &&
                            (personService.getOrAdd(receiver).getSecretSantaRecipient() == null || !personService.getOrAdd(receiver).getSecretSantaRecipient().equals(personService.getOrAdd(giver)))) {

                        System.out.printf("%s gift to %s\n", giver, receiver);
                        personService.getOrAdd(giver).setSecretSantaRecipient(personService.getOrAdd(receiver));
                        randomPeople.remove(receiver);
                        break;
                    }
                }
            }

        }
    }

    @Transactional
    public void writeOff(String input) {
        LocalDate date = dateIncludedOrNot(input);
        String openOrClose = input.contains("open") ? "open" : "close";

        switch (openOrClose) {
            case "open" -> transactionService.deleteByDateIsLessThan(date.withDayOfMonth(1));
            case "close" -> transactionService.deleteByDateIsLessThanEqual(date);
        }
    }
}




