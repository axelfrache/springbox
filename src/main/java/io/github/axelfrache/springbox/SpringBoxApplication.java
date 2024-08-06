package io.github.axelfrache.springbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.github.axelfrache.springbox")
public class SpringBoxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBoxApplication.class, args);
    }
}
