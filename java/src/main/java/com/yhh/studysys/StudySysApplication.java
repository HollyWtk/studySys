package com.yhh.studysys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.yhh.studysys.mapper.**")
public class StudySysApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudySysApplication.class, args);
    }

}
