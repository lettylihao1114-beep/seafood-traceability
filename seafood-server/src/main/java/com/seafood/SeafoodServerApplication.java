package com.seafood;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.seafood.mapper")
public class SeafoodServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeafoodServerApplication.class, args);
    }
}
