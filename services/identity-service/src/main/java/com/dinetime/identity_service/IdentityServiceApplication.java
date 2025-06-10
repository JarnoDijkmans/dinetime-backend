package com.dinetime.identity_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IdentityServiceApplication implements CommandLineRunner {

    @Value("${security.jwt-secret:NOT SET}")
    private String jwtSecret;

    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Print the first 8 characters only for safety
        System.out.println("JWT_SECRET in Java: " + 
            (jwtSecret.equals("NOT SET") ? "(not set)" : jwtSecret.substring(0, 8) + "..."));
    }
}
