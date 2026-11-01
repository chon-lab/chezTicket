package com.chezticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChezTicketApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChezTicketApplication.class, args);
    }
}
