package com.invoicehub;

import org.openxava.spring.OpenXavaApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InvoiceHubApplication extends OpenXavaApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(InvoiceHubApplication.class, args);
    }

}
