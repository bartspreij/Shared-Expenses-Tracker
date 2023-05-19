package splitter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import splitter.database.DB;
import splitter.database.GroupRepository;
import splitter.database.PersonRepository;
import splitter.database.TransactionRepository;
import splitter.sharedexpenses.SplitterLogic;
import splitter.userinterface.TextUI;

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
    public DB db(TransactionRepository transactionRepository, GroupRepository groupRepository, PersonRepository personRepository, SplitterLogic splitterLogic){
        return new DB(groupRepository, personRepository, transactionRepository, splitterLogic);
    }

    @Bean
    public TextUI textUI(Scanner scanner, SplitterLogic splitterLogic, DB db) {
        return new TextUI(scanner, splitterLogic, db);
    }
}

