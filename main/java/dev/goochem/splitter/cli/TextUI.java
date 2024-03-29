package dev.goochem.splitter.cli;

import dev.goochem.splitter.service.SplitterService;

import java.util.Scanner;

public class TextUI {
    private final Scanner scanner;
    private final SplitterService logic;
    public boolean running;

    public TextUI(Scanner scanner, SplitterService logic) {
        this.logic = logic;
        this.scanner = scanner;
        this.running = true;
    }

    public void start() {
        startSplitterControl();
    }

    private void startSplitterControl() {

        while (true) {
            String input = scanner.nextLine();
            String[] commandParts = input.split("\\s+", 2);
            UsageOption usageOption = logic.getUsageOption(input);

            switch (usageOption) {
                case HELP -> logic.displayHelp();
                case BORROW -> logic.borrow(input);
                case REPAY -> logic.repay(input);
                case BALANCE -> logic.printBalance(input);
                case BALANCE_PERFECT -> logic.printBalancePerfect(input);
                case GROUP -> logic.validateGroupInput(input);
                case PURCHASE -> logic.purchaseExtractInfo(input);
                case SECRET_SANTA -> logic.secretSanta(input.split("\\s")[1]);
                case CASH_BACK -> logic.cashBackExtractInfo(input);
                case WRITE_OFF -> logic.writeOff(input);
                case EXIT -> {
                    return;
                }
                default -> System.out.println("Unknown command. Print help to show commands list");
            }
        }
    }
}