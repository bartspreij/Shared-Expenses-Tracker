package splitter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public SplitterLogic splitterLogic() {
        return new SplitterLogic();
    }

    @Bean
    public TextUI textUI(Scanner scanner, SplitterLogic splitterLogic) {
        return new TextUI(scanner, splitterLogic);
    }

}

