package com.example.thevault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TheVaultApplication {

    //TODO JavaDoc?
    public static void main(String[] args) {
        SpringApplication.run(TheVaultApplication.class, args);

    }


}
