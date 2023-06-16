package dev.goochem.splitter.config;

import dev.goochem.splitter.cli.TextUI;
import dev.goochem.splitter.service.SplitterService;
import dev.goochem.splitter.service.GroupService;
import dev.goochem.splitter.service.PersonService;
import dev.goochem.splitter.service.TransactionService;
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
