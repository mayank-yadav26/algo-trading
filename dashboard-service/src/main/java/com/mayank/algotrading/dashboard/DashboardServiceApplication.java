package com.mayank.algotrading.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.mayank.algotrading.common.model")
public class DashboardServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DashboardServiceApplication.class, args);
    }
}
