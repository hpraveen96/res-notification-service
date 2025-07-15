package com.egov.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class CommServiceApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(CommServiceApplication.class, args);
    }

}
