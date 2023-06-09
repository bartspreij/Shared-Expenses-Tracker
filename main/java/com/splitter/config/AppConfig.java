package com.splitter.config;

import com.splitter.cli.TextUI;
import com.splitter.service.SplitterService;
import com.splitter.service.GroupService;
import com.splitter.service.PersonService;
import com.splitter.service.TransactionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
public class AppConfig {
    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }

    @Bean
    public SplitterService splitterLogic(TransactionService transactionService, GroupService groupService, PersonService personService) {
        return new SplitterService(transactionService, groupService, personService);
    }

    @Bean
    public TextUI textUI(Scanner scanner, SplitterService splitterLogic) {
        return new TextUI(scanner, splitterLogic);
    }
}
