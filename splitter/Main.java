package splitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import splitter.sharedexpenses.SplitterLogic;
import splitter.sharedexpenses.Transaction;
import splitter.userinterface.TextUI;



import java.sql.SQLException;
import java.util.Scanner;

@SpringBootApplication
@Component
public class Main implements CommandLineRunner {
    private final TextUI ui;

    @Autowired
    public Main(TextUI ui) {
        this.ui = ui;
    }

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ui.start();
    }
}
