package com.jossbee.houseOwner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class JossbeeHouseOwnerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JossbeeHouseOwnerServiceApplication.class, args);
    }

}
