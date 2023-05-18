package splitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import splitter.userinterface.TextUI;

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
    public void run(String... args) {
        ui.start();
    }
}
