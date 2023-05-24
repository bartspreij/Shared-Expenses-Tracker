package splitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import splitter.userinterface.TextUI;


@SpringBootApplication
public class Main implements CommandLineRunner {
    private final TextUI ui;

    @Autowired
    public Main(TextUI ui) {
        this.ui = ui;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.exit(0);
    }

    @Override
    public void run(String... args) throws Exception {
        ui.start();
    }
}
