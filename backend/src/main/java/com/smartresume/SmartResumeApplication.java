package com.smartresume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.smartresume")
public class SmartResumeApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartResumeApplication.class, args);
    }
}