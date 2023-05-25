package com.bartspreij.splitter;

import com.bartspreij.splitter.cli.TextUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main implements CommandLineRunner {
    private final TextUI ui;

    @Autowired
    public Main(TextUI ui) {
        this.ui = ui;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Main.class, args);
        ctx.close();
    }

    @Override
    public void run(String... args) throws Exception {
        ui.start();
    }
}
