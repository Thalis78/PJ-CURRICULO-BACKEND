package com.curriculovt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CurriculovtApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurriculovtApplication.class, args);
    }

}
