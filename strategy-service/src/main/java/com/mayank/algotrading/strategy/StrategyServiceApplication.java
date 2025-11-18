package com.mayank.algotrading.strategy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = "com.mayank.algotrading.common.model")
public class StrategyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StrategyServiceApplication.class, args);
    }
}
