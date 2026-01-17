package com.bilgehan.envanter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EnvanterApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnvanterApplication.class, args);
    }

}
