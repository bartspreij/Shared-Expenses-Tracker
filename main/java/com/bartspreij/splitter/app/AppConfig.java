package com.bartspreij.splitter.app;

import com.bartspreij.splitter.cli.TextUI;
import com.bartspreij.splitter.logic.SplitterLogic;
import com.bartspreij.splitter.repository.GroupRepository;
import com.bartspreij.splitter.repository.PersonRepository;
import com.bartspreij.splitter.repository.TransactionRepository;
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

