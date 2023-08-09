package org.mydrive;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@SpringBootApplication(scanBasePackages = {"org.mydrive"})
@MapperScan(basePackages = {"org.mydrive.mappers"})
@EnableTransactionManagement
@EnableScheduling
public class MyDriveApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyDriveApplication.class, args);
    }

}
