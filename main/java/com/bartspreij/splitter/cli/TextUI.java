package com.bartspreij.splitter.cli;

import com.bartspreij.splitter.logic.SplitterLogic;

import java.util.Scanner;

public class TextUI {
    private final Scanner scanner;
    private final SplitterLogic logic;

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
                case SECRET_SANTA -> logic.secretSanta(input);
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