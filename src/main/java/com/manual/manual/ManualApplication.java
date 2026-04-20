package com.manual.manual;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.manual.manual.mapper")
public class ManualApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManualApplication.class, args);
    }
}
