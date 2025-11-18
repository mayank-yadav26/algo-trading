package com.mayank.algotrading.broker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = "com.mayank.algotrading.common.model")
public class VirtualBrokerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VirtualBrokerServiceApplication.class, args);
    }
}
