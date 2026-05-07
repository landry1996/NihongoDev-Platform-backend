package com.nihongodev.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NihongoDevApplication {

    public static void main(String[] args) {
        SpringApplication.run(NihongoDevApplication.class, args);
    }
}
