package io.github.axelfrache.savesync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "io.github.axelfrache.savesync.repository")
@EntityScan(basePackages = "io.github.axelfrache.savesync.model")
public class SaveSyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaveSyncApplication.class, args);
    }

}
