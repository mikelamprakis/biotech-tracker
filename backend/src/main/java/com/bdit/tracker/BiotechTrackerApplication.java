package com.bdit.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BiotechTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BiotechTrackerApplication.class, args);
    }
}
