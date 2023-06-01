package com.splitter.cli;

import com.splitter.service.SplitterService;

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
            UsageOption usageOption = logic.getUsageOption(input);

            switch (usageOption) {
                case HELP -> {
                    for (UsageOption option : UsageOption.values()) {
                        if (!option.equals(UsageOption.DEFAULT)) {
                            System.out.println(option);
                        }
                    }
                }
                case BORROW, REPAY -> logic.borrowOrRepay(input);
                case BALANCE -> logic.printBalance(input);
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