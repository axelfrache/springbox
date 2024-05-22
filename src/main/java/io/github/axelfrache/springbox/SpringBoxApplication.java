package io.github.axelfrache.springbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "io.github.axelfrache.springbox.repository")
@EntityScan(basePackages = "io.github.axelfrache.springbox.model")
public class SpringBoxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBoxApplication.class, args);
    }

}
