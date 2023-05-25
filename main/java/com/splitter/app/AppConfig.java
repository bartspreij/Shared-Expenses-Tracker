package com.splitter.app;

import com.splitter.cli.TextUI;
import com.splitter.logic.SplitterLogic;
import com.splitter.repositories.GroupRepository;
import com.splitter.repositories.PersonRepository;
import com.splitter.repositories.TransactionRepository;
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
    public SplitterLogic splitterLogic(TransactionRepository transactionRepository, GroupRepository groupRepository, PersonRepository personRepository) {
        return new SplitterLogic(transactionRepository, groupRepository, personRepository);
    }

    @Bean
    public TextUI textUI(Scanner scanner, SplitterLogic splitterLogic) {
        return new TextUI(scanner, splitterLogic);
    }
}

