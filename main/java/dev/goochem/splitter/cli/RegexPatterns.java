package dev.goochem.splitter.cli;

public class RegexPatterns {
    public static final String DATE_PATTERN = "(?<date>\\d{4}[.]\\d{2}[.]\\d{2})";
    public static final String PERSONS_OR_GROUPS = "(?<personsAndOrGroups>\\(.+\\))";
    public static final String BORROW_OR_REPAY = "(?<type>[a-zA-Z]+) (?<borrower>\\w+) (?<lender>\\w+) (?<amount>\\d+[.]?\\d*)";
    public static final String EXTRACT_NAME_OR_GROUP = "[+|\\s|(|,](?<name>\\w+)";
    public static final String NAMES_TO_REMOVE = "[-](?<nameToRemove>\\w+)";
    public static final String PURCHASE_EXTRACT_INFO = "(?<=purchase\\s)(\\w+)\\s(\\w+)\\s(\\S+)\\s?" + PERSONS_OR_GROUPS;
    public static final String CASHBACK_EXTRACT_INFO = "(?<=cashBack\\s)(\\w+)\\s(\\w+)\\s(\\S+)\\s?" + PERSONS_OR_GROUPS;
    public static final String BALANCE_EXTRACT_INFO = DATE_PATTERN + "? (?<status>open|close)\\s?" + PERSONS_OR_GROUPS + "?";

        public static String getGroupRegex(String groupName) {
            return String.format("group %s (?<groupName>\\w+)\\s?" + PERSONS_OR_GROUPS + "?", groupName.toLowerCase());
        }
}
