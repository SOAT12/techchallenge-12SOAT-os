package com.fiap.soat12.os;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class OsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OsApplication.class, args);
    }

}
