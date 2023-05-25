package com.splitter.cli;

public enum UsageOption {
    BALANCE("balance"),
    BORROW("borrow"),
    CASH_BACK("cashBack"),
    EXIT("exit"),
    GROUP("group"),
    HELP("help"),
    PURCHASE("purchase"),
    REPAY("repay"),
    SECRET_SANTA("secretSanta"),
    WRITE_OFF("writeOff"),
    DEFAULT("");


    private final String nameForPrint;

    UsageOption(String nameForPrint) {
        this.nameForPrint = nameForPrint;
    }

    @Override
    public String toString() {
        return nameForPrint;
    }

}
