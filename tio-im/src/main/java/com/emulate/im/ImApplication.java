package com.emulate.im;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.tio.core.starter.annotation.EnableTioServerServer;

@SpringBootApplication
@EnableTioServerServer
public class ImApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImApplication.class,args);
    }
}
