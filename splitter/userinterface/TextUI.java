package splitter.userinterface;

import splitter.sharedexpenses.SplitterLogic;

import java.util.Scanner;

public class TextUI {
    private Scanner scanner;
    private SplitterLogic logic;


    public TextUI(Scanner scanner, SplitterLogic logic) {
        this.logic = logic;
        this.scanner = scanner;
    }

    public void start() {
        startSplitterControl();
    }

    private void startSplitterControl() {

        while (true) {
            String input = scanner.nextLine();
            UsageOption usageOption = logic.getUsageOption(input);

            switch (usageOption) {
                case HELP -> {
                    System.out.println("balance");
                    System.out.println("borrow");
                    System.out.println("exit");
                    System.out.println("group");
                    System.out.println("help");
                    System.out.println("purchase");
                    System.out.println("repay");
                }
                case BORROW, REPAY -> logic.borrowOrRepay(input);
                case BALANCE -> logic.getBalance(input);
                case GROUP -> logic.validateGroupInput(input);
                case PURCHASE -> logic.purchaseExtractInfo(input);
                case EXIT -> {
                    return;
                }
                default -> System.out.println("Unknown command. Print help to show commands list");
            }
        }
    }
}